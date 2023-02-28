# java-filmorate
Template repository for Filmorate project.
![DataBase structure](https://github.com/MSmirnov2407/java-filmorate/blob/main/QuickDBD-export.png)

# SQL запросы
## Получение всех фильмов
SELECT FIlm_Id,
		Film_name
FROM FIlm;

## Получение всех пользователей
SELECT Login,
		User_name
FROM User;

## Получение топ 10 популярных фильмов
SELECT Film_Id
	   COUNT(L.Login) AS Like_amount
FROM Film AS F
INNER JOIN Like AS L ON L.FIlm_Id = F.Film_Id
GROUP_BY Film_Id
ORDER BY Like_amount DESC
LIMIT 10

## Получение общих друзей двух пользователей c login88 и login99. Status = 1 - подтвержденная дружба
В подзапросах выбирается список друзей пользователя login99.
В основном запросе выбираются друзья пользователя login88, которые входят в список
друзей пользователя login99.
И в подзапросе и в основном запросе используется UNION, т.к. при создании записи дружбы пользователь login88
мог попасть как в поле User_1 так и в поле User_2. (аналогично для пользователя login99)

SELECT User_1
FROM Friendship
WHERE User_2 = 'Login88' AND Status = 1
				AND User_1 IN (

								SELECT User_2
								FROM Friendship
								WHERE User_1 = 'Login99' AND Status = 1

								UNION

								SELECT User_1
								FROM Friendship
								WHERE User_2 = 'Login99' AND Status = 1
								)
UNION
SELECT User_2
FROM Friendship
WHERE User_1= 'Login88' AND Status = 1
				AND User_2 IN (

								SELECT User_2
								FROM Friendship
								WHERE User_1 = 'Login99' AND Status = 1

								UNION

								SELECT User_1
								FROM Friendship
								WHERE User_2 = 'Login99' AND Status = 1
								)
