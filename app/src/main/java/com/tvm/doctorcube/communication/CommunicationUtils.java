package com.tvm.doctorcube.communication;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.tvm.doctorcube.R;
import com.google.android.material.card.MaterialCardView;

public class CommunicationUtils {
    private final Context context;
    private final String phoneNumber;
    private final String whatsappNumber;
    private final String whatsappMessage;
    private static final String DEFAULT_PHONE = "+1234567890";
    private static final String DEFAULT_WHATSAPP = "919730037126";
    private static final String DEFAULT_MESSAGE = "Hello, I would like to inquire about studying medicine abroad.";

    // Constructor with default values
    public CommunicationUtils(Context context) {
        this.context = context;
        this.phoneNumber = DEFAULT_PHONE;
        this.whatsappNumber = DEFAULT_WHATSAPP;
        this.whatsappMessage = DEFAULT_MESSAGE;
    }

    // Constructor with custom values
    public CommunicationUtils(Context context, String phoneNumber,
                              String whatsappNumber, String whatsappMessage) {
        this.context = context;
        this.phoneNumber = phoneNumber != null ? phoneNumber : DEFAULT_PHONE;
        this.whatsappNumber = whatsappNumber != null ? whatsappNumber : DEFAULT_WHATSAPP;
        this.whatsappMessage = whatsappMessage != null ? whatsappMessage : DEFAULT_MESSAGE;
    }

    public void setupCommunicationButtons(View view) {
        MaterialCardView callButton = view.findViewById(R.id.call_now_button);
        MaterialCardView whatsappButton = view.findViewById(R.id.whatsapp_button);

        setupCallButton(callButton);
        setupWhatsappButton(whatsappButton);
    }

    private void setupCallButton(MaterialCardView callButton) {
        if (callButton != null) {
            callButton.setOnClickListener(v -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                try {
                    context.startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupWhatsappButton(MaterialCardView whatsappButton) {
        if (whatsappButton != null) {
            whatsappButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" +
                            whatsappNumber + "&text=" + Uri.encode(whatsappMessage)));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Please install WhatsApp first", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
