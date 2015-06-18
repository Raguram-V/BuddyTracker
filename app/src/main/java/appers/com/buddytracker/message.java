package appers.com.buddytracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class message extends ActionBarActivity {
    EditText msgText;
    Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        Utility.confActionBar(getSupportActionBar());
        msgText = (EditText) this.findViewById(R.id.msgText);
        btnSend = (Button) this.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = msgText.getText().toString();
                if(!(str.isEmpty())){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT,str);
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                }else {
                    Utility.showCustomAlert(getLayoutInflater(),
                            message.this,false,"Please Enter a Message");
                }
            }
        });
    }
}
