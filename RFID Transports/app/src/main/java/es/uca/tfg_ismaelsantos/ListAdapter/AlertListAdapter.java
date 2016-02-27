package es.uca.tfg_ismaelsantos.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import es.uca.tfg_ismaelsantos.Clases.Alerta;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.R;

/**
 * Created by Ismael Santos Cabaña on 18/11/15.
 * ListAdapter para incluir alerta en lista
 */
public class AlertListAdapter extends BaseAdapter{


    private Activity activity;
    private List<Alerta> alertas;
    private static LayoutInflater inflater = null;


    /**
     * Constructor
     * @param activity
     * @param alertas
     */
    public AlertListAdapter(Activity activity, List<Alerta> alertas){
        this.activity = activity;
        this.alertas = alertas;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return alertas.size();
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
            vi = inflater.inflate(R.layout.list_item_alert,null);
            holder = new ViewHolder();
            holder.nombre = (TextView)vi.findViewById(R.id.nombre);
            holder.date = (TextView)vi.findViewById(R.id.date);
            holder.desfase = (TextView)vi.findViewById(R.id.desfase);
            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }


        Alerta item = alertas.get(position);
        holder.nombre.setText(" "+alertas.get(position).getId());
        holder.date.setText(item.getsFecha_creacion());
        DecimalFormat decimales = new DecimalFormat("0.00");
        double saldoDb = item.getSaldo_bd();
        double saldoTarjeta = item.getSaldo_tarjeta();
        double desfase = (saldoDb - saldoTarjeta)/200.0;
        holder.desfase.setText(" "+decimales.format(Math.abs(desfase))+"€");


        return vi;
    }


    public void setData(List<Alerta> alertas){
        this.alertas.addAll(alertas);
        this.notifyDataSetChanged();
    }


    public void remove(int position){
        alertas.remove(position);
        this.notifyDataSetChanged();
    }

    public class ViewHolder
    {
        TextView nombre;
        TextView date;
        TextView desfase;
    }




}
