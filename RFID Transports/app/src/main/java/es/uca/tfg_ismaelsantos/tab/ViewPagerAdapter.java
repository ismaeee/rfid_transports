package es.uca.tfg_ismaelsantos.tab;

/**
 * Created by Ismael Santos Cabaña on 20/10/15.
 */
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertas;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentAlertasUsuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentTarjetas;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentTarjetasUsuario;
import es.uca.tfg_ismaelsantos.notificaciones_rfid.FragmentUsuarios;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    private Context mContext;
    private int imageResId[];
    private int rol;
    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, int imageResId[], Context mContext,int rol) {
        super(fm);
        this.NumbOfTabs = imageResId.length;
        this.mContext = mContext;
        this.imageResId = imageResId;
        this.rol = rol;
    }


    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Log.d("TFG_ViewPagerAdapter","ROL: "+rol);
        if((rol==1)||(rol==3)) { //Si es admin o superadmin
            if (position == 0) {
                FragmentUsuarios fragmentUsuarios = new FragmentUsuarios();
                return fragmentUsuarios;
            } else if (position == 1) {
                FragmentAlertas fragmentAlertas = new FragmentAlertas();
                return fragmentAlertas;
            } else {
                FragmentTarjetas fragmentTarjetas = new FragmentTarjetas();
                return fragmentTarjetas;
            }
        }else{
            if (position == 0) {
                FragmentTarjetasUsuario fragmentTarjetasUsuario = new FragmentTarjetasUsuario();
                return fragmentTarjetasUsuario;

            } else {
                FragmentAlertasUsuario fragmentAlertasUsuario = new FragmentAlertasUsuario();
                return fragmentAlertasUsuario;
            }
        }

    }



    @Override
    public CharSequence getPageTitle(int position) {

        Drawable image = mContext.getResources().getDrawable(imageResId[position]);

        //int dimIconTab = (int)mContext.getResources().getDimension(R.dimen.iconTab);
        //Log.d("NextGo_IconTab", "Dimension: " + image.getIntrinsicHeight());

        image.setBounds(0, 0, image.getIntrinsicHeight(), image.getIntrinsicWidth());

        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }


    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }


}