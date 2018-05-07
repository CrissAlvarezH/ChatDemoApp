package miercoles.dsl.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EmptyStackException;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NOMBRE = "nombre";
    public static final String EXTRA_USUARIOS  = "usuarios";
    private final String TAG = "ActividadSocket";


    private Socket socket;
    private OnNuevoTexto onNuevoTexto;
    private EditText edtTexto;
    private String nombre;
    private Spinner spnConectados;
    private RecyclerView recyclerMensajes;
    private MensajeAdapter mensajesAdapter;

    {
        try {
            socket = IO.socket("http://167.99.52.15:3000");
        } catch (URISyntaxException e) {
            Log.e(TAG, "Erro al crear el socket");
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTexto = findViewById(R.id.edt_texto);
        spnConectados = findViewById(R.id.spn_conectados);
        recyclerMensajes = findViewById(R.id.recycler_mensajes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);// Manda el scroll al final
        recyclerMensajes.setLayoutManager(layoutManager);

        mensajesAdapter = new MensajeAdapter(new ArrayList<Mensaje>());
        recyclerMensajes.setAdapter(mensajesAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getString(EXTRA_NOMBRE) != null){
            nombre = extras.getString(EXTRA_NOMBRE);
            String[] usuarios = extras.getStringArray(EXTRA_USUARIOS);

            Log.v(TAG, "Longitud de usuarios "+usuarios.length);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, usuarios);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnConectados.setAdapter( adapter );

            if(getSupportActionBar() != null){
                getSupportActionBar().setTitle(nombre);
            }
        }else{
            Toast.makeText(this, "Debe loguearse", Toast.LENGTH_SHORT).show();
            finish();
        }


        onNuevoTexto = new OnNuevoTexto();

        socket.on("nuevoTexto", onNuevoTexto);// agregamos un listener
        socket.on("listarUsuarios", onListarUsuarios);

        socket.connect();// Nos conectamos al socket
    }

    public void clickEnviar(View btn){
        JSONObject jsonEnviar = new JSONObject();
        try {
            jsonEnviar.put("para", spnConectados.getSelectedItem().toString());
            jsonEnviar.put("de", nombre);
            jsonEnviar.put("texto", edtTexto.getText().toString());

            socket.emit("nuevoTexto", jsonEnviar.toString());

            Mensaje mensaje = new Mensaje("", edtTexto.getText().toString(), Mensaje.ENVIADO);
            mensajesAdapter.addMensaje(mensaje);

            edtTexto.setText("");
            recyclerMensajes.smoothScrollToPosition( mensajesAdapter.getItemCount() - 1 );
        } catch (JSONException e) {
            Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private class OnNuevoTexto implements Emitter.Listener {

        private String texto;

        @Override
        public void call(final Object... args) {
            Log.v(TAG, "Nuevo evento");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject json = (JSONObject) args[0];
                        texto = json.getString("texto");
                        String de = json.getString("de");

                        Mensaje mensaje = new Mensaje(de, texto, Mensaje.RECIVIDO);
                        mensajesAdapter.addMensaje(mensaje);
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Error al obtener texto", Toast.LENGTH_SHORT).show();

                        Log.e(TAG, "Error al obtener texto del JSON");
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private Emitter.Listener onListarUsuarios = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG, "Listar argumentos"+ args[0]);
            final JSONArray usuariosJson = (JSONArray) args[0];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String [] usuarios = new String[usuariosJson.length()];

                    for(int i=0; i<usuariosJson.length(); i++){
                        try {
                            usuarios[i] = usuariosJson.getString(i);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Error casteando los usuarios", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, usuarios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnConectados.setAdapter( adapter );
                }
            });

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
        if(onNuevoTexto != null)
            socket.off("nuevoTexto", onNuevoTexto);// quitamos el listener

        if(onListarUsuarios != null)
            socket.off("listarUsuarios", onListarUsuarios);
    }
}
