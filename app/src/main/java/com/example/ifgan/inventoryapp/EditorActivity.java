package com.example.ifgan.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ifgan.inventoryapp.data.InventoryContract.InvEntry;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_INV_LOADER = 0;
    private static final int SELECT_PICTURE = 100;

    ImageView imgView;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentInvUri;

    /**
     * EditText field to enter the product's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the product's amount
     */
    private EditText mAmountEditText;

    /**
     * EditText field to enter the product's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the product's sold
     */
    private EditText mSoldEditText;

    /**
     * EditText field to enter the product's provider
     */
    private EditText mProviderEditText;

    /**
     * EditText field to enter the product's provider e-mail
     */
    private EditText mProviderEmailEditText;

    /**
     * Boolean flag that keeps track of whether the prod has been edited (true) or not (false)
     */
    private boolean mProdHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProdHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentInvUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentInvUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.editor_activity_title_new_prod));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_prod));
            mSoldEditText = (EditText) findViewById(R.id.edit_prod_sold);
            mAmountEditText = (EditText) findViewById(R.id.edit_prod_amount);
            mSoldEditText.setEnabled(false);
            mAmountEditText.setEnabled(false);


            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INV_LOADER, null, this);
        }

        imgView = (ImageView) findViewById(R.id.imgView);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_prod_name);
        mAmountEditText = (EditText) findViewById(R.id.edit_prod_amount);
        mPriceEditText = (EditText) findViewById(R.id.edit_prod_price);
        mSoldEditText = (EditText) findViewById(R.id.edit_prod_sold);
        mProviderEditText = (EditText) findViewById(R.id.edit_prod_provider);
        mProviderEmailEditText = (EditText) findViewById(R.id.edit_prod_provider_email);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mAmountEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSoldEditText.setOnTouchListener(mTouchListener);
        mProviderEditText.setOnTouchListener(mTouchListener);
        mProviderEmailEditText.setOnTouchListener(mTouchListener);

        ImageButton btnLess = (ImageButton) findViewById(R.id.lessAmount);
        ImageButton btnMore = (ImageButton) findViewById(R.id.moreAmount);

        btnLess.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                lessAmount();
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                MoreAmount();
            }
        });

    }

    public void lessAmount(){

        mAmountEditText = (EditText) findViewById(R.id.edit_prod_amount);
        String amountString = mAmountEditText.getText().toString().trim();
        int amount = Integer.parseInt(amountString);

        amount -= 1;

        if (amount < 0)
        {
            Toast.makeText(this, "The value can't be negative",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            mAmountEditText.setText(String.valueOf(amount));
        }

    }

    public void MoreAmount(){

        mAmountEditText = (EditText) findViewById(R.id.edit_prod_amount);
        String amountString = mAmountEditText.getText().toString().trim();
        int amount = Integer.parseInt(amountString);

        amount += 1;

        mAmountEditText.setText(String.valueOf(amount));

    }

    public void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();

                if (null != selectedImageUri) {
                    imgView.setImageURI(selectedImageUri);
                }
            }
        }
    }

    /**
     * Get user input from editor and save prod into database.
     */
    private boolean saveProd() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String amountString = mAmountEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String soldString = mSoldEditText.getText().toString().trim();
        String providerString = mProviderEditText.getText().toString().trim();
        String providerEmailString = mProviderEmailEditText.getText().toString().trim();

        // Check if this is supposed to be a new prod
        // and check if all the fields in the editor are blank
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(amountString) ||
                TextUtils.isEmpty(priceString) || TextUtils.isEmpty(soldString) ||
                TextUtils.isEmpty(providerString) || TextUtils.isEmpty(providerEmailString)) {

            Toast.makeText(this, getString(R.string.empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InvEntry.COLUMN_PRODUCT_AMOUNT, amountString);
        values.put(InvEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InvEntry.COLUMN_PRODUCT_PROVIDER, providerString);
        values.put(InvEntry.COLUMN_PRODUCT_PROVIDER_EMAIL, providerEmailString);

        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int sold = 0;
        if (!TextUtils.isEmpty(soldString)) {
            sold = Integer.parseInt(soldString);
        }
        values.put(InvEntry.COLUMN_PRODUCT_SOLD, sold);

        imgView.setDrawingCacheEnabled(true);

        Bitmap bmap = imgView.getDrawingCache();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] img = bos.toByteArray();

        values.put(InvEntry.COLUMN_PRODUCT_IMAGE, img);


        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentInvUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(InvEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInvUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
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

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentInvUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            MenuItem menuItemBuy = menu.findItem(R.id.action_buy);
            MenuItem menuItemSell = menu.findItem(R.id.action_sell);
            menuItem.setVisible(false);
            menuItemBuy.setVisible(false);
            menuItemSell.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                if (saveProd()) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_buy:
                sendEmail();
                return true;
            case R.id.action_sell:
                showSellConfirmationDialog();
                return true;
            case R.id.action_photo:
                openImageChooser();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProdHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProdHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_AMOUNT,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_SOLD,
                InvEntry.COLUMN_PRODUCT_PROVIDER,
                InvEntry.COLUMN_PRODUCT_PROVIDER_EMAIL,
                InvEntry.COLUMN_PRODUCT_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInvUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
            int amountColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_AMOUNT);
            int priceColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
            int soldColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_SOLD);
            int providerColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PROVIDER);
            int providerEmailColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PROVIDER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int amount = cursor.getInt(amountColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int sold = cursor.getInt(soldColumnIndex);
            String provider = cursor.getString(providerColumnIndex);
            String providerEmail = cursor.getString(providerEmailColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);

            Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
            imgView.setImageBitmap(b);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAmountEditText.setText(Integer.toString(amount));
            mPriceEditText.setText(Integer.toString(price));
            mSoldEditText.setText(Integer.toString(sold));
            mProviderEditText.setText(provider);
            mProviderEmailEditText.setText(providerEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAmountEditText.setText("");
        mPriceEditText.setText("");
        mSoldEditText.setText("");
        mProviderEditText.setText("");
        mProviderEmailEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSellConfirmationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle(getString(R.string.dialogsellmessage));
        dialogBuilder.setMessage(getString(R.string.dialogsellmessage2));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mSoldEditText = (EditText) findViewById(R.id.edit_prod_sold);
                int amount = Integer.parseInt(mAmountEditText.getText().toString());
                int sold = Integer.parseInt(edt.getText().toString());

                if (sold > amount) {
                    Toast.makeText(EditorActivity.this, getString(R.string.enough), Toast.LENGTH_SHORT).show();
                } else {

                    mSoldEditText.setText(edt.getText().toString());
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentInvUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInvUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    /**
     * Sendemail to provider
     */
    private void sendEmail() {

        EditText editEmailProvider = (EditText) findViewById(R.id.edit_prod_provider_email);
        String emailString = editEmailProvider.getText().toString().trim();

        EditText editNameText = (EditText) findViewById(R.id.edit_prod_name);


        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailString});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Buy more " + editNameText.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I want more " + editNameText.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EditorActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
