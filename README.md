# Descripción
Esta aplicación utiliza Socket.io para conectarse con un [servidor escrito en Nodejs](https://github.com/CrissAlvarezH/chatDemoServer) para 
crear una comunicación bidireccional cliente-servidor en tiempo real. Al crear la comunicación se realiza un registro mediente un 
login en el server con un nickname, con dicho nickname los demas usuarios pueden enviar mensaje privados que solo les llegarán a ese usuario.

# Capturas de pantalla

Debajo se puede ver la aplicación y los logs del server trabajando, emulando un chat entre dos usuarios.

* Empezamos logueandonos con dos usuarios (Cristian y Juan) en dos dispositivos distintos.

 ![Demo chat app](https://crissalvarezh.github.io/ImagenesRepos/imgs/chatDemoApp/loguearse.gif)

* Despues podemos elegir, en el seleccionable de la parte superior de la pantalla, el usuario al que queremos enviarle 
un mensaje privado.

![Demo chat app](https://crissalvarezh.github.io/ImagenesRepos/imgs/chatDemoApp/seleccionar_usuario_destino.gif)

* De igual manera el destinatario puede responder los mensajes

![Demo chat app](https://crissalvarezh.github.io/ImagenesRepos/imgs/chatDemoApp/responder_mjs.gif)

* Mientras esto sucede el servidor va mostrando los respectivos logs

![Demo chat app](https://crissalvarezh.github.io/ImagenesRepos/imgs/chatDemoApp/demo_server_chat_node.gif)

# Proximas mejoras

1. Agregar aviso de disponibilidad de red
2. Agregar "Escribiendo..." y "En linea" a los usuarios de chat
