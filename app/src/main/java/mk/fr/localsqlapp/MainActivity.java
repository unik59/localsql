package mk.fr.localsqlapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.mk.database.ContactDAO;
import fr.mk.database.DatabaseHandler;
import mk.fr.localsqlapp.model.Contact;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView contactListView;
    private List<Contact> contactList;
    private Integer selectedIndex;
    private Contact selectedPersonn;
    private final String LIVE_CYCLE = "cycle_de_vie";
    private ContactDAO dao;
    private DatabaseHandler db;



    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LIVE_CYCLE, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LIVE_CYCLE, "onPause");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LIVE_CYCLE, "onRestart");

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LIVE_CYCLE, "onResume");

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LIVE_CYCLE, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LIVE_CYCLE, "onDestroy");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(this.selectedIndex !=null){
        outState.putInt("selectedIndex", this.selectedIndex);}
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LIVE_CYCLE, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.db = new DatabaseHandler(this);

        this.dao =new ContactDAO(this.db);




        //Reference au widget ListView sur le layout
        contactListView = findViewById(R.id.contactListView);


       contactListInit();

        //recuperation des données persistées dans le bundle
        if (savedInstanceState != null) {
            this.selectedIndex = savedInstanceState.getInt("selectedIndex");
            if (this.selectedIndex != null) {
                this.selectedPersonn = this.contactList.get(this.selectedIndex);
                contactListView.setSelection(this.selectedIndex);
            }
        }


        //Création d'un contactArrayAdapter
        ContactArrayAdapter contactAdapter = new ContactArrayAdapter(this, contactList);

        //Definition de l'adapter de notre listView
        contactListView.setAdapter(contactAdapter);

        contactListView.setOnItemClickListener(this);

    }

    /**
     * lancement de l'activité formulaire en appuyant sur le bouton
     *
     * @param view
     */

    public void onAddContact(View view) {


        Intent formIntent = new Intent(this, FormActivity.class);
        if (this.selectedIndex != null) {
            editContact(formIntent);
        }
        startActivityForResult(formIntent, 1);

    }

    private void editContact(Intent formIntent) {
        this.selectedPersonn = this.contactList.get(selectedIndex);
        formIntent.putExtra("id", String.valueOf(selectedPersonn.getId()));
        formIntent.putExtra("name", selectedPersonn.getName());
        formIntent.putExtra("firstName", selectedPersonn.getFirst_name());
        formIntent.putExtra("email", selectedPersonn.getEmail());
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long ivd) {


        this.selectedIndex = position;
        this.selectedPersonn = this.contactList.get(selectedIndex);


        Toast.makeText(this, "Contact supprimé " + selectedPersonn.getFirst_name(), Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenuOptionDelete:
                deleteSelectedContact();
                break;
            case R.id.mainMenuOptionEdit:
                onAddContact(item.getActionView());
                break;

        }
        return true;
    }

    public void deleteSelectedContact() {

        if (this.selectedIndex != null) {


            //Instanciation de la connexion à la base de données
            DatabaseHandler db = new DatabaseHandler(getBaseContext());


            //Insertion des données
            try {
                String sql = "DELETE From contacts WHERE id=?";
                String[] params = {String.valueOf(this.selectedPersonn.getId())};

                // db.getWritableDatabase().execSQL(sql,params);
                db.getWritableDatabase().delete("contacts", "id" + "=" + selectedPersonn.getId(), null);
                //db.getWritableDatabase().delete("contacts", "id=?"  , params);


                contactListInit();

            } catch (SQLiteException ex) {
                Log.e("SQL EXCEPTION", ex.getMessage());
                Toast.makeText(this, "Impossible de supprimer", Toast.LENGTH_SHORT).show();
            }

        } else

        {
            Toast.makeText(this, "Vous devez selectionner un contact", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Mise a jour effectué", Toast.LENGTH_SHORT).show();

            this.contactListInit();

        }
    }

    private void contactListInit() {


        //Recuperation de la liste des contacts
        contactList = this.dao.findAll();

        //Création d'un contactArrayAdapter
        ContactArrayAdapter contactAdapter = new ContactArrayAdapter(this, contactList);

        //Definition de l'adapter de notre listView
        contactListView.setAdapter(contactAdapter);


    }
}
