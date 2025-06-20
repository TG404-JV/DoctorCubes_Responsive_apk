package com.tvm.doctorcube;


import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tvm.doctorcube.R; // Make sure this import is correct for your project


public class CustomToast {

    /**
     * Displays a custom toast message.
     *
     * @param activity The activity context.
     * @param message  The text to display in the toast.
     */
    public static void showToast(Activity activity, String message) {
        // Inflate the custom toast layout
        LayoutInflater inflater = activity.getLayoutInflater();
        View toastView = inflater.inflate(R.layout.custom_toast, null); // Ensure this matches your XML file name

        // Get the image and text view from the layout
        ImageView toastIcon = toastView.findViewById(R.id.toast_icon);
        TextView toastTitle = toastView.findViewById(R.id.toast_title);
        TextView toastText = toastView.findViewById(R.id.toast_text);

        // Set the icon, title, and text.  You can customize these.
        toastIcon.setImageResource(R.drawable.logo_doctor_cubes_dynamic); // Use your actual image resource
        toastTitle.setText("DoctorCubes");  //set the title
        toastText.setText(message);

        // Create the toast object
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG); // You can change the duration
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100); // Adjust position as needed
        toast.setView(toastView);

        // Show the toast
        toast.show();
    }
}
