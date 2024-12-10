CRUD service - endpoint /todos/

Todo
- описание 
- статус

 create todo - json 
 update todo  id, json
 // read todo - websocket // wait message
 delete todo - id 
 
Search GET /todos?offset=1limit=2

{
todo1 
todo2
}

Принципы программирования: 
DRY - не повторяй себя
KISS - делай код простым 
YAGNI - тебе это не понадобится 
SOLID - 
- одна сущность - одна отвественность
- код должен быть не модифицируемым, но расширяемым 
- наследники и родители взаимозаменяемые в коде 
- один контракт - одна задача
- инвертируем зависимости

RequestSpecification 

