package com.snapper.emvcardreader;


import com.snapper.emvcardreader.util.CardInfo;
import com.snapper.emvcardreader.util.EMVReader;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	 private NfcAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAdapter = NfcAdapter.getDefaultAdapter(this);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		if(this.mAdapter != null){
			this.mAdapter.disableForegroundDispatch(this);
		}
	}
	
	@Override
    protected void onResume()
    {
        super.onResume();

        if (this.mAdapter != null)
        {
            this.ensureSensorIsOn();

            // Listen to all incoming NFC tags that support the IsoDep interface
            mAdapter.enableForegroundDispatch(this,
                    PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0),
                    new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) },
                    new String[][] { new String[] { IsoDep.class.getName() }});
        }
    }
	
	@Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        // Is the intent for a new NFC tag discovery?
        if (intent != null && intent.getAction() == NfcAdapter.ACTION_TECH_DISCOVERED){
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep isoDep = IsoDep.get(tag);

            // Does the tag support the IsoDep interface?
            if (isoDep == null){
                return;
            }
            
            new CardInfo(){
            	@Override
                protected void onPostExecute(CardInfo card){
            		if(card != null){
            			try{
		            		EMVReader reader = new EMVReader(card, null, card.getADF());
		            		reader.doTrace = true;
		            		reader.read();
		                  
		        			//reader.
		        			TextView issuer = (TextView)findViewById(R.id.card_issuer);
		        			
		        			issuer.setText(getString(R.string.card_issuer) + ":" + reader.issuer);
		        			
		        			TextView cardExpiryMonth = (TextView)findViewById(R.id.card_expiry_month);
		        			cardExpiryMonth.setText(getString(R.string.expiry_month) + ":" + reader.expiryMonth);
		        			
		        			TextView cardExpiryYr = (TextView)findViewById(R.id.card_expiry_yr);
		        			cardExpiryYr.setText(getString(R.string.expiry_yr) + ":" + reader.expiryYear);
            			}catch(Exception err){
            				Log.e("Error", err.toString());
            			}
            		}
                }
            }.execute(isoDep);
        }
    }
	
	private void ensureSensorIsOn()
    {
        if(!this.mAdapter.isEnabled())
        {
            // Alert the user that NFC is off
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.nfc_title))
                .setMessage(getString(R.string.nfc_msg))
                .setPositiveButton(getString(R.string.nfc_ok_btn), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // Send the user to the settings page and hope they turn it on
                        if (android.os.Build.VERSION.SDK_INT >= 16)
                        {
                            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                        }
                        else
                        {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }
                })
                .setNegativeButton(getString(R.string.nfc_cancel_btn), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // Do nothing
                    }
                })
                .show();
        }
    }
}
