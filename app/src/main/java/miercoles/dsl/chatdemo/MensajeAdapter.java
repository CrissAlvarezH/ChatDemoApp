package miercoles.dsl.chatdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>{
    private ArrayList<Mensaje> mensajes;

    public MensajeAdapter(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public class MensajeViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout layoutMensaje;
        public TextView txtNombre, txtMensaje;

        public MensajeViewHolder(View itemView) {
            super(itemView);

            layoutMensaje = itemView.findViewById(R.id.layout_contenido_item_mensaje);
            txtNombre = itemView.findViewById(R.id.item_txt_nombre);
            txtMensaje = itemView.findViewById(R.id.item_txt_mensaje);
        }
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_mensaje, parent, false);

        return new MensajeViewHolder( item );
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get( position );

        if(mensaje.getTipo() == Mensaje.RECIVIDO){
            holder.layoutMensaje.setGravity( Gravity.LEFT );
            holder.txtNombre.setVisibility(View.VISIBLE);
        }else{
            holder.layoutMensaje.setGravity( Gravity.RIGHT );
            holder.txtNombre.setVisibility(View.GONE);
        }

        holder.txtNombre.setText( mensaje.getNombre() );
        holder.txtMensaje.setText( mensaje.getMensaje() );
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public void addMensaje(Mensaje mensaje){
        mensajes.add( mensaje );
        notifyDataSetChanged();
    }
}
