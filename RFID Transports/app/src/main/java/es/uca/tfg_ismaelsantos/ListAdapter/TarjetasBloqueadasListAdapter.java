package es.uca.tfg_ismaelsantos.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;

/**
 * Created by Ismael Santos Cabaña on 24/11/15.
 * ListAdapter para incluir tarjetas bloquedas en lista
 */
public class TarjetasBloqueadasListAdapter extends BaseAdapter{


    private Activity activity;
    private List<Tarjeta> tarjetas;
    private static LayoutInflater inflater = null;


    public TarjetasBloqueadasListAdapter(Activity activity, List<Tarjeta> tarjetas){
        this.activity = activity;
        this.tarjetas = tarjetas;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tarjetas.size();
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
            vi = inflater.inflate(R.layout.list_item_tarjetas_bloqueadas,null);
            holder = new ViewHolder();
            holder.uid = (TextView)vi.findViewById(R.id.uid);
            holder.usuario= (TextView)vi.findViewById(R.id.usuario);
            holder.fecha = (TextView)vi.findViewById(R.id.fecha);

            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }


        Tarjeta item = tarjetas.get(position);
        holder.uid.setText(item.getUid());
        holder.usuario.setText(item.getNombreUsuario());
        holder.fecha.setText(item.getFechaBloqueo());
        return vi;
    }


    public void setData(List<Tarjeta> tarjetas){
        this.tarjetas.addAll(tarjetas);
        this.notifyDataSetChanged();
    }


    public void remove(int position){
        tarjetas.remove(position);
        this.notifyDataSetChanged();
    }

    public class ViewHolder
    {
        TextView uid;
        TextView usuario;
        TextView fecha;

    }




}
