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
 * Created by Ismael Santos Cabaña on 18/11/15.
 * ListAdapter para incluir tarjeta en lista
 */
public class TarjetaListAdapter extends BaseAdapter{


    private Activity activity;
    private List<Tarjeta> tarjetas;
    private static LayoutInflater inflater = null;
    private int rol;

    public TarjetaListAdapter(Activity activity, List<Tarjeta> tarjetas){
        this.activity = activity;
        this.tarjetas = tarjetas;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TarjetaListAdapter(Activity activity,List<Tarjeta> tarjetas, int rol){
        this.rol = rol;
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
            vi = inflater.inflate(R.layout.list_item_tarjetas,null);
            holder = new ViewHolder();
            holder.title = (TextView)vi.findViewById(R.id.uid);
            holder.subtitle= (TextView)vi.findViewById(R.id.usuario);
            holder.imageView = (ImageView)vi.findViewById(R.id.alert);
            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }


        Tarjeta item = tarjetas.get(position);
        holder.title.setText(item.getUid());
        if(tarjetas.get(position).getBloqueada() == 1){ //Si está bloqueda la imagen que muestra es esta
            holder.imageView.setImageResource(R.mipmap.tarjeta_bloqueada_mini);
        }else{
            holder.imageView.setImageResource(R.mipmap.tarjeta_red_mini);
        }
        if(activity!= null){
            holder.subtitle.setText(item.getNombreUsuario());
        }else{
            holder.subtitle.setText(item.getSaldo()/200.0+"€");
        }


        return vi;
    }


    public void setData(List<Tarjeta> tarjetas){
        for ( Tarjeta tarjeta : tarjetas){
            Log.d("TFG_AdapterTarjeta",""+tarjeta.toString());
        }

        this.tarjetas.addAll(tarjetas);
        this.notifyDataSetChanged();
    }


    public void setData(Tarjeta tarjeta, int position) {
        this.tarjetas.set(position,tarjeta);
        this.notifyDataSetChanged();
    }





    public void remove(int position){
        tarjetas.remove(position);
        this.notifyDataSetChanged();
    }

    public class ViewHolder
    {
        ImageView imageView;
        TextView title;
        TextView subtitle;

    }




}
