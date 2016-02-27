package es.uca.tfg_ismaelsantos.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Usuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;

/**
 * Created by Ismael Santos Cabaña on 29/11/15.
 * ListAdapter para incluir usuarios en lista
 */
public class UsuarioListAdapter extends BaseAdapter{


    private Activity activity;
    private List<Usuario> usuarios;
    private static LayoutInflater inflater = null;


    public UsuarioListAdapter(Activity activity, List<Usuario> usuarios){
        this.activity = activity;
        this.usuarios = usuarios;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.list_item_usuario,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView)vi.findViewById(R.id.circle_image);
            holder.nombre = (TextView)vi.findViewById(R.id.nombre);
            holder.username = (TextView)vi.findViewById(R.id.username);
            holder.dni = (TextView)vi.findViewById(R.id.dni);
            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }


        Usuario item = usuarios.get(position);
        holder.nombre.setText(item.getNombre()+" "+item.getApellidos());
        holder.dni.setText(item.getDni());
        holder.username.setText(item.getUser());
        Log.d("PRUEBAS", "Muestra usuario: ");
        Log.d("PRUEBAS", "Muestra usuario: " + usuarios.get(position).toString());


        switch(usuarios.get(position).getRol()){
            case 1: {
                holder.imageView.setImageResource(R.mipmap.ic_admin);
                holder.imageView.setBackgroundResource(R.drawable.border_admin);
            } break;
            case 2: {
                holder.imageView.setImageResource(R.mipmap.ic_user);
                holder.imageView.setBackgroundResource(R.drawable.border_user);
            }break;
            case 3: {
                holder.imageView.setImageResource(R.mipmap.ic_super_admin);
                holder.imageView.setBackgroundResource(R.drawable.border_super_admin);
            } break;
        }


        return vi;
    }


    public void setData(List<Usuario> usuarios){
        this.usuarios.addAll(usuarios);
        this.notifyDataSetChanged();
    }

    public void setData(Usuario usuario,int position){
        this.usuarios.set(position,usuario);
        this.notifyDataSetChanged();
    }



    public void remove(int position){
        usuarios.remove(position);
        this.notifyDataSetChanged();
    }

    public class ViewHolder
    {
        ImageView imageView;
        TextView nombre;
        TextView dni;
        TextView username;
    }




}
