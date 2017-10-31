EMVCardReader
=============

Simple NFC EMV Card Reader that display Track 2 information from a PayPass/PayWave card.

* Uses the Android's [foreground dispatch system](http://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc.html#foreground-dispatch) to communicate with NFC tags only when the activity is in the foreground
* Warn user if the device's NFC is turned off
* Pass obtained NFC tags to EMVCardReader api to extract card information on a background thread

Usage:

```
new CardInfo(){
            	@Override
                protected void onPostExecute(CardInfo card){
            		if(card != null){
            			try {
                            // EMV card reader library
		            		EMVReader reader = new EMVReader(card, null, card.getADF());
		            		reader.doTrace = true;
		            		reader.read();
		            		
		            		System.out.println("Issuer "+reader.issuer);
		            		System.out.println("Result "+reader.pan+",Y="+reader.expiryYear+",M="+reader.expiryMonth);
            			} catch(Exception err){
            				Log.e("Error", err.toString());
            			}
            		}
                }
            }.execute(isoDep);
```
