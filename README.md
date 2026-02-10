Aquí vamos a ir añadiendo toda la información del proyecto hogarLink, vamos a dividir su desarrollo en sprints:
- Sprint 1, Entorno de trabajo común (15-22 de Septiembre): Configuración del IDE eclipse con maven, spring, un jdk común para que no haya conflictos, y una estructura de carpetas adecuada para tener front y back juntos. Configuración de un repositorio de git y creación de todas las ramas para trabajar de forma común. Esto dará lugar a la versión
        1.0.0 → subida del proyecto (eclipse) a GitHub.
  
- Sprint 2, Realización de pruebas iniciales (23-29 de Septiembre): Procedemos con la creación de entidades, interfaces y controladores de prueba para comprobar que la aplicación se despliega de forma adecuada en local, y que los métodos GET y POST funcionan adecuadamente. Estas pruebas están basadas en los contenidos vistos en las clases de laboratorio correspondientes. También se procede a la creación de varios .html para probar funcionalidad conjunta entre front y back. Se da lugar a las versiones:
        1.0.1 → Commit — .gitignore (archivos OS y logs) Cambios solo en configuración del repositorio, sin afectar funcionalidad ni código fuente. (24 Sept)
        1.0.2 → Commit — Plan de desarrollo (README inicial) Solo documentación del proyecto (planificación, estructura, sprints). (24 Sept)
        1.1.0 → Commit — WebController.java + inicio.html. Se añade un controlador y una vista inicial, creando la primera funcionalidad accesible desde navegador. (29 Sept)
        1.2.0 → Commit — Greeting.java, greeting.html, result.html. Se añade una entidad (Greeting), controladores @GetMapping y @PostMapping, y páginas nuevas para formulario y                   resultado. Amplía el sistema con nuevas rutas (/greeting). (29 Sept)
        1.2.1 → Commit — Actualización de README (timeline). Cambio puramente documental, sin código ni dependencias. (29 Sept).
  
- Sprint 3, Creación de la bbdd en local y arreglo de ramas en github (30-14 de Octubre): Descargamos el mongoDB Compass y el mongoDB Atlas para la creación de una bbdd llamada prueba con una única tabla llamada users. Realizamos pruebas en local enlazando la base de datos con nuestro proyecto para probar la nueva funcionalidad implementada (añadir users), una vez validado que funciona bien y los users se añaden correctamente, procedemos a la creación de las ramas de github en base a las necesidades de nuestro proyecto (features, hotfix, development y main) ya que las que teniamos antes no eran correctas. Después, ampliamos la funcionalidad de la bbdd prueba, permitiendo que todos los integrantes puedan acceder con sus credenciales. Realizamos pruebas para ver que se pueden añadir users desde distintos equipos y que la tabla se actualiza correctamente, permitiendo a cada usuario ver lo que habia añadido otro. Esto corresponde a las versiones:
        1.3.0 → Commit — Integración MongoDB. Se añaden dependencias de spring-boot-starter-data-mongodb y configuración en application.properties. Introduce una nueva tecnología                  (MongoDB), pero no cambia el comportamiento actual si no se usa. (1 Oct).
        1.3.1 → Commit — Merge development con feature/inicio-sesion. Cambios menores y fusiones en greeting.java, result.html, greeting.html. No rompe compatibilidad, solo                        integra mejoras de otra rama. (1 Oct)
        1.4.0 → Commit — Formulario User + conexión a MongoDB (2 Oct) Se crean: User.java y UserRepository.java. Controladores en WebController para /users. Formulario en                          greeting.html para insertar y listar usuarios. Ahora la aplicación tiene un CRUD parcial con MongoDB. Esto es una nueva funcionalidad significativa, pero mantiene                  compatibilidad.
        1.4.1 → Commit — Configuración MongoDB Atlas (2 Oct).

- Sprint 4, Corrección de toda la documentación necesaria (14-20 de Octubre): Corregimos todas las cosas que nos faltaban de la documentación de cara a la primera defensa del proyecto, se corrige el README, se hace un control de versiones mejorado, y se crea un plan de gestión de configuración. Versiones:
        1.4.2 → Commit — Corrección del README.

- Sprint 5, Creación de la bbdd al completo con todas las tablas y comenzar con la programación del back (21-27 de Octubre): Aquí la intención es renombrar la bbdd prueba al nombre real que usemos y añadir todas las tablas junto con sus atributos. Además crearemos las entidades en el back, junto con sus metodos correspondientes y procederemos a hacer pruebas para ver que lo implementado funcione, este sería el Sprint que nos toca la semana que viene. Versiones: Pendiente de determinar en base a lo que consigamos implementar.

INSTRUCCIONES DE EJECUCIÓN

1º Abrir una terminal de eclipse (Cerciorarse de que estamos en la carpeta del proyecto)

<img width="2202" height="260" alt="image" src="https://github.com/user-attachments/assets/11800513-eee0-453a-afa0-449972d4de4a" />

2º Introducir el comando: " mvn spring-boot:run "

<img width="1644" height="708" alt="image" src="https://github.com/user-attachments/assets/bceaaf99-514f-40fd-a61e-404e83df97fc" />

3º Una vez el programa está corriendo, meterse en el buscador que se tenga por defecto y poner: "localhost:8080/inicio o simplemente localhost:8080" y saldrá la página de inicio y ya tendremos el sw funcionando.

<img width="2880" height="1596" alt="image" src="https://github.com/user-attachments/assets/3c658270-7e44-4efb-8695-82c896c0743f" />

4º Para cerrar la ejecución del programa, basta con hacer Ctrl+C
