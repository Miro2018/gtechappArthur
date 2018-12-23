package dev.com.br.gtechapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import dev.com.br.gtechapp.R;
import dev.com.br.gtechapp.adapter.TabsPagerAdapter;
import dev.com.br.gtechapp.fragments.DashboardFragment;
import dev.com.br.gtechapp.fragments.PerfilFragment;
import dev.com.br.gtechapp.fragments.RegistrosFragment;

public class MainActivity extends BaseActivity {

    public static String POSITION = "POSITION";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Fragment regFragment;

    TabsPagerAdapter adapter;

    private int[] tabIcons = {
//            R.drawable.ic_input_black_24dp,
            R.drawable.ic_home_w_24dp,
            R.drawable.ic_pie_chart_w_24dp,
//            R.drawable.ic_bar_chart,
            R.drawable.ic_person_w_24dp
//            R.drawable.ic_more_horiz_black_24dp

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        adapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        setUpActionBar();

    }

    private void setUpActionBar(){

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);

    }

    @Override
    public void onBackPressed() {

        showProgressDialogSair();
    }

    public void showProgressDialogSair() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Gostaria de sair do aplicativo?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                builder.
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {

//        adapter.addFrag(new InputFragment(), "Inserir");
        adapter.addFrag(new RegistrosFragment(), "");
        adapter.addFrag(new DashboardFragment(), "");
        adapter.addFrag(new PerfilFragment(), "");
//        adapter.addFrag(new ConfigFragment(), "");
        viewPager.setAdapter(adapter);
    }
}
