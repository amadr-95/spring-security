# Spring Data JPA y PostgreSQL
![schema-applicattion](src/main/resources/static/images/schema-applicattion.png)
Proyecto de ejemplo usando el módulo de Spring Data JPA con conexión a una BD PostgreSQL.  
Se realizan las operaciones básicas CRUD (Create, Read, Update, Delete) desde la clase `StudentService` (la lógica de la aplicación) que usa la interfaz `StudentRepository`.

## Read
Con la anotación `@GetMapping` sobre el path _http://localhost:8080/api/v1/student_ se obtiene una lista con los estudiantes.  
Para que la BD no esté vacía en el arranque se introducen estudiantes de ejemplo en la clase `StudentConfig` con la anotación `@Bean`.

## Create
Con la anotación `@PostMapping` sobre el mismo path pero pasandole un objeto _Student_ se guarda éste en la BD.
Primero se hace una comprobación que valida que el mail no está ya en uso. En caso de que esté en uso se lanza una excepción con un mensaje descriptivo.
Se usa Postman para pasarle a este servicio un _body_ (`@RequestBody`) con los datos del nuevo estudiante.
Ej:
``{
"name": "Javi",  
"email": "javi@gmail.com",  
"birth": "1956-07-17"  
}``

## Delete
En este caso usamos la anotación ``@DeleteMapping`` sobre el mismo path pero añadiendo el _ID_ del estudiante
como variable (`@PathVariable`) por la que buscar para borrarlo: _http://localhost:8080/api/v1/student/id_. Si el _ID_ no existe se lanza una excepcion
y en caso contrario se elimina de la BD.

## Update (put)
Por ultimo para actualizar un registro usamos la anotación `@PutMapping` sobre el mismo path añadiendo tambien el _ID_ del estudiante
como variable (`@PathVariable`) para buscarlo y además dos valores más _name_ e _email_ con la anotación `@RequestParam` (opcionales) que serán los campos a actualizar:
_http://localhost:8080/api/v1/student/ID?name=name&email=email_  
En el servicio se marca este método con la anotación `@Transactional` que nos permite simplemente actualizar los campos haciendo uso de los _set_ de la entidad _Student_.  
Se comprueba:
- Que el ID existe (para poder actualizar)
- Que el nombre sea válido (no nulo, diferente del actual)
- Que el email sea válido (no nulo, diferente del actual, etc)
- Que el mail no esté ya en uso por otro estudiante (se lanza excepcion)  

Si todo eso se cumple, se actualiza el registro.




