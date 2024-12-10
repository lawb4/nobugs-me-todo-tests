CrudInterface 
- create;
- update;
- delete;

SearchInterface
- readAll;

TodoRequest extends Request implemement CrudInterface, SearchInterface {
    Response 
}

ValidatedTodoRequest extends Request implemement CrudInterface, SearchInterface {
    Todo  todoRequest.create()
.assertThat.statusCode(201)
. сериализация в объект 

Request
- RequestSpecification 

TodoRequest(unauth)
TodoRequest(admin)
TodoRequest(manager)

--- 
не параметризируем по юзеру 

TodoRequest() {
create() {
if (stage == """) 
}

todoRequest.create()