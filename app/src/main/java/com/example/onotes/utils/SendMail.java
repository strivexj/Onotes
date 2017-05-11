package com.example.onotes.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.onotes.App;
import com.example.onotes.R;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<Void, Void, Void> {


    private Context context;
    private Session session;
    private String email;
    private String subject;
    private String message;
    private String myEmail = App.getContext().getString(R.string.myemail);
    private String myPassword = App.getContext().getString(R.string.mypassword);

    private ProgressDialog progressDialog;


    public SendMail(Context context, String email, String message) {

        this.context = context;
        this.email = email;
        this.subject =App.getContext().getString(R.string.subject);
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = ProgressDialog.show(context, App.getContext().getString(R.string.progressDialog1), App.getContext().getString(R.string.progressDialog2), false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressDialog.dismiss();

        //Toast.makeText(context, , Toast.LENGTH_LONG).show();
        ToastUtil.showToast(App.getContext().getString(R.string.sending_successfully));
    }

    @Override
    protected Void doInBackground(Void... params) {

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");


        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(myEmail, myPassword);
                    }
                });

        try {

            MimeMessage mm = new MimeMessage(session);

            mm.setFrom(new InternetAddress("Onotes"));

            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            mm.setSubject(subject);

            mm.setText(message);

            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
