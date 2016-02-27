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

import es.uca.tfg_ismaelsantos.Clases.Tarjeta;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;

/**
 * Created by Ismael Santos Cabaña on 25/11/15.
 * ListAdapter para incluir tarjetas de usuarios en lista
 */
public class TarjetasDeUsuarioListAdapter extends BaseAdapter{


    private Activity activity;
    private List<Tarjeta> tarjetas;
    private static LayoutInflater inflater = null;


    public TarjetasDeUsuarioListAdapter(Activity activity, List<Tarjeta> tarjetas){
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

        Log.d("TFG_GetView","Se ejecuta");

        if(convertView==null){
            vi = inflater.inflate(R.layout.list_user_tarjetas,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView)vi.findViewById(R.id.imagenTarjeta);
            holder.idTarjeta = (TextView)vi.findViewById(R.id.idTarjeta);
            holder.uid = (TextView)vi.findViewById(R.id.uid);
            holder.saldo = (TextView)vi.findViewById(R.id.saldo);
            holder.fechaActualizacion = (TextView)vi.findViewById(R.id.fechaActualizacion);
            holder.fechaCreacion = (TextView)vi.findViewById(R.id.fechaCreacion);
            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }


        Tarjeta item = tarjetas.get(position);

        if(item.getBloqueada() == 1){
            holder.imageView.setImageResource(R.mipmap.tarjeta_bloqueada_mini);
        }else{
            holder.imageView.setImageResource(R.mipmap.tarjeta_red_mini);
        }
        holder.idTarjeta.setText(" " + item.getId());
        holder.uid.setText(" "+item.getUid());
        holder.saldo.setText(" "+item.getSaldo()/200.0+"€");
        holder.fechaActualizacion.setText(" "+ item.getFecha_actualizacion());
        holder.fechaCreacion.setText(" "+ item.getFecha_creacion());

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
        ImageView imageView;
        TextView idTarjeta;
        TextView uid;
        TextView saldo;
        TextView fechaActualizacion;
        TextView fechaCreacion;

    }




}
