package com.example.ifgan.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ifgan.inventoryapp.data.InvContract;
import com.example.ifgan.inventoryapp.data.InvContract.InvEntry;

/**
 * Created by ifgan on 04/09/2017.
 */

public class InvCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InvCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView amountTextView = (TextView) view.findViewById(R.id.amount);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView soldTextView = (TextView) view.findViewById(R.id.sold);


        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
        int amountColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_AMOUNT);
        int priceColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
        int soldColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_SOLD);

        // Read the pet attributes from the Cursor for the current pet
        String prodName = cursor.getString(nameColumnIndex);
        int amount = cursor.getInt(amountColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int sold = cursor.getInt(soldColumnIndex);

        nameTextView.setText(prodName);
        amountTextView.setText(Integer.toString(amount));
        priceTextView.setText(Integer.toString(price));
        soldTextView.setText(Integer.toString(sold));
    }
}
