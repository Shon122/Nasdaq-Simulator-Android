package com.gtappdevelopers.bankrehovot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    private ArrayList<User> userList;
    private ArrayList<User> originalList;


    public UserAdapter(Context context, ArrayList<User> userList) {
        super(context, 0, userList);
        this.userList = userList;
        this.originalList = MainActivity.users;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
        }
        // Lookup view for data population
        TextView nameView = convertView.findViewById(R.id.nameOfUser);
        TextView balanceView = convertView.findViewById(R.id.balanceOfUser);
        ImageView profile = convertView.findViewById(R.id.profile_image_item);
        // Populate the data into the template view using the data object
        nameView.setText(user.username);
        balanceView.setText(user.balance + "$");
        String savedImage = user.savedImage;
        if (savedImage != null && !savedImage.equals("null") && savedImage.length() > 10) {
            byte[] decodedString = Base64.decode(savedImage, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
            profile.setBackgroundColor(Color.WHITE);
            profile.setImageBitmap(circularBitmap);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(),
                    bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getWidth(),
                    Bitmap.Config.ARGB_8888
            );
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


}