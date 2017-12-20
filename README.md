# Инструкция по запуску:
1) Установить PostgreSQL и создать чистую базу "avito". 
База должна быть доступна по URL jdbc:postgresql://localhost:5432/avito
Для доступа к базе создать роль 
	username = p2p
	password = n2bkjErp2ppNvW
В случае необходимости, настройки можно поменять в src/main/resources/application.properties и src/main/resources/liquibase.properties

2) Перейти в папку с проектом и выполнить "mvn jetty:run"
Схема и все необходимые данные в БД будут созданы при первом запуске

3) Для удобства проверки в папке "chrome_postman_dump" лежит коллеция для chrome:postman, которую можно импортировать (там есть все методы для работы с API)


# Описание системных ролей и пользователей:
1) Роли: "ROLE_VIEW_ORDERS", "ROLE_SEARCH_ORDERS", "ROLE_AUTHORISED_USER", "ROLE_ADMIN"
2) Пользователи: "admin", "vieworders", "searchorders", "authoriseduser". Пароль для всех пользователей "admin"
"admin" - может выполнять любые действия
"vieworders" - только просмотр списка заказов (без фильтрации)
"searchorders" - только просмотр списка заказов (с фильтрацией)
"authoriseduser" - любые действия по просмотру записей (без создания, редактирования и удаления), кроме просмотра списка заказов


# Описание API

	Логин/логаут:
http://localhost:8080/api/auth/login - POST запрос. Необходимо передать username и password в форме "x-form-urlencoded"
http://localhost:8080/api/auth/logout - POST запрос. Выполняет логаут
http://localhost:8080/api/auth/validate - POST запрос для проверки логина и просмотра списка ролей

	Работа с заказами:
http://localhost:8080/api/orders?limit=10&offset=0&productVendorCode=123&creationDateStart=2016-01-01T00%3A00%3A00.000%2B0000&creationDateEnd=2016-01-01T00%3A00%3A01.000%2B0000 - GET запрос. Просмотр списка заказов с фильтрацией. limit и offset - обязательные параметры

http://localhost:8080/api/orders?limit=10&offset=0 - GET запрос. Просмотр списка заказов без фильтрации. limit и offset - обязательные параметры

http://localhost:8080/api/orders - POST зарос. В формате JSON передать описание заказа. Пример: 
{"number":11, "state": "NEW", "creationDate": "2016-01-01T00:00:00.000+0000"}

http://localhost:8080/api/orders/50 - PUT запрос. Редактирование заказа. В формате JSON передать описание заказа.

http://localhost:8080/api/orders/showByNumber?number=1 - GET запрос. Поиск по номеру.

http://localhost:8080/api/orders/11 - DELETE запрос. Удаление заказа.

	Работа с продуктом:
http://localhost:8080/api/products?limit=10&offset=0&priceMin=10&priceMax=20&namePart=name - GET запрос. Просмотр списка продуктов. limit и offset - обязательные параметры.

http://localhost:8080/api/products - POST зарос. В формате JSON передать описание продукта. Пример: 
{"vendorCode":"vendorCode", "name": "name", "price": 12.12}

http://localhost:8080/api/products/190 - PUT запрос. Редактирование продукта. В формате JSON передать описание продукта.

http://localhost:8080/api/products/showByVendorCode?vendorCode=vendorCode -  GET запрос. Поиск по артикулу.

http://localhost:8080/api/products/131 - DELETE запрос. Удаление продукта.

	Работа с позицией заказа:
http://localhost:8080/api/orders/50/orderProducts - POST зарос. В формате JSON передать описание позиции (номер заказа берётся из URL). Пример: 
{"product":130, "amount": "2"}

http://localhost:8080/api/orders/50/orderProducts/220 - DELETE запрос. Удаление позиции заказа.






		
