package fr.eni.abono.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import fr.eni.abono.R;
import fr.eni.abono.bo.Frequency;
import fr.eni.abono.bo.Priority;
import fr.eni.abono.bo.Subscription;
import fr.eni.abono.dao.AppDatabase;
import fr.eni.abono.dao.Connexion;

public class DetailsActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPrice;
    private EditText editTextDescription;
    private Spinner priorityDropDown;
    private Spinner frequencyDropDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextDescription = findViewById(R.id.editTextDescription);
        frequencyDropDown = findViewById(R.id.frequencyDropDown);
        priorityDropDown = findViewById(R.id.priorityDropDown);

        ArrayAdapter<CharSequence> frequencyAdapter =  ArrayAdapter.createFromResource(
                this,
                R.array.frequency_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        frequencyDropDown.setAdapter(frequencyAdapter);

        ArrayAdapter<CharSequence> priorityAdapter =  ArrayAdapter.createFromResource(
                this,
                R.array.priority_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        priorityDropDown.setAdapter(priorityAdapter);

        // test vérification extras
        if(getIntent().getExtras() != null) {
            Subscription item = (Subscription) getIntent().getExtras().get("object");

            editTextName.setText(item.getName());
            editTextPrice.setText(String.valueOf(item.getPrice()));
            switch (item.getFrequency()) {
                case DAILY:
                    frequencyDropDown.setSelection(0);
                    break;
                case WEEKLY:
                    frequencyDropDown.setSelection(1);
                    break;
                case MONTHLY:
                    frequencyDropDown.setSelection(2);
                    break;
                case QUARTERLY:
                    frequencyDropDown.setSelection(3);
                    break;
                case SEMESTERLY:
                    frequencyDropDown.setSelection(4);
                    break;
                case ANNUALLY:
                    frequencyDropDown.setSelection(5);
            }

            editTextDescription.setText(item.getDescription());
            switch (item.getPriority()) {
                case INDISPENSABLE:
                    priorityDropDown.setSelection(0);
                    break;
                case IMPORTANT:
                    priorityDropDown.setSelection(1);
                    break;
                case OPTIONAL:
                    priorityDropDown.setSelection(2);
                    break;
                default:
                    break;
            }
        }
    }

    public void validSubscription(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Subscription item = (Subscription) getIntent().getExtras().get("object");
                item.setPrice(Float.parseFloat(editTextPrice.getText().toString()));
                switch (frequencyDropDown.getSelectedItemPosition()) {
                    case 0:
                        item.setFrequency(Frequency.DAILY);
                        break;
                    case 1:
                        item.setFrequency(Frequency.WEEKLY);
                        break;
                    case 2:
                        item.setFrequency(Frequency.MONTHLY);
                        break;
                    case 3:
                        item.setFrequency(Frequency.QUARTERLY);
                        break;
                    case 4:
                        item.setFrequency(Frequency.SEMESTERLY);
                        break;
                    case 5:
                        item.setFrequency(Frequency.ANNUALLY);
                }
                item.setName(String.valueOf(editTextName.getText()));
                item.setDescription(String.valueOf(editTextDescription.getText()));
                AppDatabase db = Connexion.getConnexion(DetailsActivity.this);
                db.subscriptionDao().update(item);
            }
        }).start();

        Log.d("validSubscription", "Subscription added in database");

        Intent intentAddSubscription = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intentAddSubscription);
    }

    public void removeSubscription(View view) {
        final Subscription item = (Subscription) getIntent().getExtras().get("object");

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Connexion.getConnexion(DetailsActivity.this);
                db.subscriptionDao().delete(item);
            }
        }).start();

        Log.d("removeSubscription", "Subscription deleted from database");

        Intent intentAddSubscription = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intentAddSubscription);
    }
}
