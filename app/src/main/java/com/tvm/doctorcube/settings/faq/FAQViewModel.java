package com.tvm.doctorcube.settings.faq;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class FAQViewModel extends ViewModel {
    private MutableLiveData<List<FAQItem>> faqList = new MutableLiveData<>();

    public LiveData<List<FAQItem>> getFaqList() {
        return faqList;
    }

    public FAQViewModel() {
        loadFAQs();
    }

    private void loadFAQs() {
        List<FAQItem> faqs = new ArrayList<>();
        faqs.add(new FAQItem("What countries do DoctorCubes help with for MBBS admissions?", "DoctorCubes specializes in guiding Indian students to top medical universities in Russia and other countries around the world."));
        faqs.add(new FAQItem("How many years of experience does DoctorCubes have?", "DoctorCubes has 6+ years of experience in the field of international medical education."));
        faqs.add(new FAQItem("How many Russian universities does DoctorCubes have partnerships with?", "DoctorCubes has exclusive partnerships with over 12 Russian universities."));
        faqs.add(new FAQItem("What kind of support does DoctorCubes provide?", "DoctorCubes provides end-to-end support, including counseling, admission support, visa and travel assistance, hostel accommodation, and ongoing support throughout your medical education."));
        faqs.add(new FAQItem("Are the medical programs offered through DoctorCubes affordable?", "Yes, DoctorCubes helps you access quality medical education at affordable fees without compromising on academic standards."));
        faqs.add(new FAQItem("Does DoctorCubes assist with visa applications?", "Yes, our experienced team guides you through visa application and travel arrangements for a hassle-free journey."));
        faqs.add(new FAQItem("Does DoctorCubes help with hostel accommodation?", "Yes, we ensure comfortable and safe accommodation options for all our students studying abroad."));
        faqs.add(new FAQItem("Is there ongoing support after admission?", "Yes, our relationship doesn't end at admission - we provide continuous support throughout your medical education journey."));
        faqs.add(new FAQItem("How can I contact DoctorCubes for a free counseling session?", "You can contact us via phone at +91 7517036564 or email at doctorscube71@gmail.com, or visit our offices in Russia, Chh. Sambhaajinagar (Aurangabad), and Delhi."));
        faqs.add(new FAQItem("Where are DoctorCubes' offices located?", "We have offices in Russia (Бульвар строителей 43 Кемерово Россия), India- Chh. Sambhaajinagar (Kranti chowk,near santh eknath mandir above Punjab and Sindh bank 2nd floor Aurangabad Maharashtra 431003), and India- Delhi."));
        faqs.add(new FAQItem("What are the working hours of DoctorCubes?", "Our working hours are Mon - Sat: 9:00 AM - 5:00 PM."));
        faqs.add(new FAQItem("How many students have been successfully placed by DoctorCubes?", "DoctorCubes has successfully placed 10000+ students."));
        faqs.add(new FAQItem("How many partner universities does DoctorCubes have?", "DoctorCubes has 50+ partner universities."));
        faqs.add(new FAQItem("How many countries does DoctorCubes cover?", "DoctorCubes covers 10+ countries."));
        faqs.add(new FAQItem("How do I reset my password?", "Go to settings and select 'Reset Password'."));
        faqs.add(new FAQItem("What is the application process?", "The application process starts with a free counselling, followed by document submission, university application, visa processing and travel arrangements."));
        faqs.add(new FAQItem("What documents are required for admission?", "Documents required include passport, 10th and 12th grade certificates, NEET score card, and other relevant academic documents."));
        faqs.add(new FAQItem("What is the language of instruction in Russian medical universities?", "The language of instruction is primarily English, with some universities offering Russian language courses."));
        faqs.add(new FAQItem("Is NEET required to study MBBS abroad through DoctorCubes?", "Yes, NEET qualification is required for Indian students seeking MBBS admission abroad."));
        faqs.add(new FAQItem("What is the average duration of MBBS in Russia?", "The average duration of MBBS in Russia is 6 years."));

        // App Specific FAQs
        faqs.add(new FAQItem("How do I create an account on the DoctorCubes app?", "Download the app, open it, and tap on 'Sign Up'. Fill in the required details and verify your email."));
        faqs.add(new FAQItem("How do I reset my password in the DoctorCubes app?", "Go to 'Profile' or 'Settings' and select 'Reset Password'. Follow the on-screen instructions."));
        faqs.add(new FAQItem("Can I track my application status through the app?", "Yes, you can track your application status, receive updates, and view important documents directly through the app."));
        faqs.add(new FAQItem("How do I contact my assigned counselor through the app?", "You can contact your counselor through the 'Chat' or 'Messages' section within the app."));
        faqs.add(new FAQItem("Can I access university brochures and information on the app?", "Yes, the app provides access to detailed university brochures, course information, and campus details."));
        faqs.add(new FAQItem("How do I upload my documents through the DoctorCubes app?", "Go to the 'Documents' section and follow the instructions to upload your required documents."));
        faqs.add(new FAQItem("Can I schedule appointments with DoctorCubes through the app?", "Yes, you can schedule counseling sessions and other appointments through the app's scheduling feature."));
        faqs.add(new FAQItem("How do I receive notifications about my application and visa status?", "Ensure notifications are enabled in your app settings. You will receive real-time updates through push notifications."));
        faqs.add(new FAQItem("Is there a forum or community feature on the DoctorCubes app?", "Yes, the app features a community forum where you can connect with other students and share experiences."));
        faqs.add(new FAQItem("How do I update my profile information in the app?", "Go to 'Profile' and tap on 'Edit Profile' to update your personal information."));
        faqs.add(new FAQItem("What kind of support is available through the app's chat feature?", "You can get instant support for technical issues, application queries, and general information through the app's chat feature."));
        faqs.add(new FAQItem("Can I pay application fees through the app?", "Yes, you can securely pay application and other related fees through the app's payment gateway."));
        faqs.add(new FAQItem("How do I download important documents from the app?", "Navigate to the 'Documents' section, select the document, and tap on the download icon."));
        faqs.add(new FAQItem("Can I get information about accommodation through the app?", "Yes, the app provides information about available hostel and accommodation options near the universities."));
        faqs.add(new FAQItem("How do I provide feedback about the DoctorCubes app?", "Go to 'Settings' and select 'Feedback' to share your thoughts and suggestions."));

        faqList.setValue(faqs);
    }
}

