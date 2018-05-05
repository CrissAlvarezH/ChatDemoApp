package miercoles.dsl.chatdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActividad";

    private EditText edtNombre;

    private Socket socket;


    {
        try {
            socket = IO.socket("http://192.168.100.53:3000");
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error al crear el socket");
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtNombre = findViewById(R.id.edt_nombre);

        socket.on("usuarioAdd", onUsuarioRegistrado);
        socket.connect();
    }

    public void clickEntrar(View btn){
        if(!edtNombre.getText().toString().trim().isEmpty()) {
            socket.emit("nuevoUsuario", edtNombre.getText().toString());
        }else{
            edtNombre.setError("Escriba su nombre");
            edtNombre.requestFocus();
        }
    }

    private Emitter.Listener onUsuarioRegistrado = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String mensaje = args[0].toString();

            if(mensaje != null && mensaje.equals("usuario_add")){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_NOMBRE, edtNombre.getText().toString());
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        socket.off("usuarioAdd");
    }
}
