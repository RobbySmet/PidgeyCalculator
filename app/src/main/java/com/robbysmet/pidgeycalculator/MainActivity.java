package com.robbysmet.pidgeycalculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    public static final int TIME_TO_EVOLVE = 30;
    public static final int CANDIES_TO_EVOLVE = 12;

    @BindView(R.id.edt_pokemon_amount)
    EditText mPokemonAmountEditText;
    @BindView(R.id.edt_nr_of_candies)
    EditText mNrOfCandiesEditText;
    @BindView(R.id.btn_calculate)
    Button mCalculateButton;

    // Result container
    @BindView(R.id.result_container)
    LinearLayout mResultContainer;
    @BindView(R.id.transfer_count)
    TextView mTransferCountLabel;
    @BindView(R.id.evolve_count)
    TextView mEvolveCountLabel;
    @BindView(R.id.duration)
    TextView mDurationLabel;
    @BindView(R.id.result)
    TextView mResultLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mNrOfCandiesEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    calculate();
                    dismissKeyboard(v);
                    return true;
                }
                return false;
            }
        });
    }

    private void dismissKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick(R.id.btn_calculate)
    public void onClick() {
        calculate();
    }

    private void calculate() {
        // Get input amounts
        int pokemon = Integer.parseInt(mPokemonAmountEditText.getText().toString());
        int candies = Integer.parseInt(mNrOfCandiesEditText.getText().toString());

        // Counters
        int evolveCount = 0;
        int transferCount = 0;

        // How many can be evolved without transfers
        boolean canStillEvolve = true;
        while (canStillEvolve) {
            // Not enough candies to evolve
            if ((candies / CANDIES_TO_EVOLVE) == 0) {
                canStillEvolve = false;
            } else {
                pokemon--;
                candies -= CANDIES_TO_EVOLVE; // Remove the candy
                candies++; // Gain 1 candy per evolution
                evolveCount++;

                // No more pokemon left
                if (pokemon == 0) {
                    break;
                }
            }
        }

        if (pokemon > 0) {
            while ((candies + pokemon) >= (CANDIES_TO_EVOLVE + 1)) {
                // Keep transferring until enough candies
                while (candies < CANDIES_TO_EVOLVE) {
                    transferCount++;
                    pokemon--;
                    candies++;
                }

                // Evolve a Pokemon
                pokemon--;
                candies -= CANDIES_TO_EVOLVE;
                candies++;
                evolveCount++;
            }
        }

        showResult(pokemon, candies, evolveCount, transferCount);
    }

    private void showResult(int pokemonLeft, int candiesLeft, int evolveCount, int transferCount) {
        mResultContainer.setVisibility(View.VISIBLE);

        mTransferCountLabel.setText(fromHtml("You should transfer <b>" + transferCount + "</b> Pidgeys before activating your Lucky Egg</p>"));
        mEvolveCountLabel.setText(fromHtml("You will be able to evolve <b>" + evolveCount + "</b> Pidgeys, gaining <b>" + evolveCount * 1000 + "</b> XP"));
        mDurationLabel.setText(fromHtml("On average, it will take about <b>" + (evolveCount * TIME_TO_EVOLVE / 60) + "</b> minutes to evolve your Pidgeys"));
        mResultLabel.setText(fromHtml("You will have <b>" + pokemonLeft + "</b> Pidgeys and <b>" + candiesLeft + "</b> candies left over"));
    }

    private Spanned fromHtml(String text) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }
}
