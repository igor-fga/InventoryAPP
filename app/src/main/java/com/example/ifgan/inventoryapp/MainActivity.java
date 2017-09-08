package com.example.ifgan.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ifgan.inventoryapp.data.InventoryContract;
import com.example.ifgan.inventoryapp.data.InventoryContract.InvEntry;
import com.example.ifgan.inventoryapp.data.InventoryProvider;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the prod data loader
     */
    private static final int INV_LOADER = 0;

    /**
     * Adapter for the ListView
     */
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
        mCursorAdapter = new InvCursorAdapter(this, null);
        invListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        invListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(InvEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(INV_LOADER, null, this);

    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllProds() {
        int rowsDeleted = getContentResolver().delete(InvEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_AMOUNT,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_SOLD,
                InvEntry.COLUMN_PRODUCT_PROVIDER,
                InvEntry.COLUMN_PRODUCT_PROVIDER_EMAIL};

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

    public void sellProduct(int id, int sold , int amount){

        sold += 1;

        if (sold > amount) {
            Toast.makeText(MainActivity.this, getString(R.string.enough), Toast.LENGTH_SHORT).show();
        }else
        {
            ContentValues values = new ContentValues();
            values.put(InvEntry.COLUMN_PRODUCT_SOLD, sold);

            final Uri CONTENT_URI_ID = Uri.withAppendedPath(InvEntry.CONTENT_URI, "/" + id);

            int rowsAffected = getContentResolver().update(CONTENT_URI_ID, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }




}
