package com.example.ifgan.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ifgan.inventoryapp.data.InvContract.InvEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int INV_LOADER = 0;

    /** Adapter for the ListView */
    InvCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the inventory data
        ListView invListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        invListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of inventory data in the Cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InvCursorAdapter(this, null);
        invListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        invListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(InvEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(INV_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertProd() {
        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME, "Notebook");
        values.put(InvEntry.COLUMN_PRODUCT_AMOUNT, 3);
        values.put(InvEntry.COLUMN_PRODUCT_PRICE, 1000);
        values.put(InvEntry.COLUMN_PRODUCT_SOLD, 0);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(InvEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllProds() {
        int rowsDeleted = getContentResolver().delete(InvEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProd();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProds();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_AMOUNT,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_SOLD};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InvEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
