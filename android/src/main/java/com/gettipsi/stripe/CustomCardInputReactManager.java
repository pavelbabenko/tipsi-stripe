package com.gettipsi.stripe;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;

import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.stripe.android.view.CardInputListener;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;

import java.util.Objects;

/**
 * Created by dmitriy on 11/15/16
 */

public class CustomCardInputReactManager extends SimpleViewManager<CardInputWidget> {

  public static final String REACT_CLASS = "TPSCardField";
  private static final String TAG = CustomCardInputReactManager.class.getSimpleName();
  private static final String NUMBER = "number";
  private static final String EXP_MONTH = "expMonth";
  private static final String EXP_YEAR = "expYear";
  private static final String CCV = "cvc";

  private ThemedReactContext reactContext;
  private WritableMap currentParams;

  private String currentNumber;
  private int currentMonth;
  private int currentYear;
  private String currentCCV;

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  protected CardInputWidget createViewInstance(final ThemedReactContext reactContext) {

    XmlPullParser parser = reactContext.getResources().getXml(R.xml.stub_element);
    try {
      parser.next();
      parser.nextTag();
    } catch (Exception e) {
      e.printStackTrace();
    }


    AttributeSet attr = Xml.asAttributeSet(parser);

    final CardInputWidget cardInputWidget = new CardInputWidget(reactContext, attr);


    setListeners(cardInputWidget);

    this.reactContext = reactContext;
    cardInputWidget.post(new Runnable() {
      @Override
      public void run() {
        InputMethodManager inputMethodManager = (InputMethodManager) reactContext.getSystemService(reactContext.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(cardInputWidget.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
        // cardInputWidget.requestFocus();
      }
    });
    return cardInputWidget;
  }

  @ReactProp(name = "enabled")
  public void setEnabled(CardInputWidget view, boolean enabled) {
    view.setEnabled(enabled);
  }

  @ReactProp(name = "backgroundColor")
  public void setBackgroundColor(CardInputWidget view, int color) {
    Log.d("TAG", "setBackgroundColor: " + color);
    view.setBackgroundColor(color);
  }

  @ReactProp(name = "cardNumber")
  public void setCardNumber(CardInputWidget view, String cardNumber) {
    // view.setCardNumber(view.getCard().getNumber());
    view.setCardNumber(cardNumber);
  }

  @ReactProp(name = "expDate")
  public void setExpDate(CardInputWidget view, String expDate) {
    view.setExpiryDate(view.getCard().getExpMonth(), view.getCard().getExpYear());
    /*  view.setExpDate(expDate, true); */
  }

  @ReactProp(name = "securityCode")
  public void setSecurityCode(CardInputWidget view, String securityCode) {
    view.setCvcCode(securityCode);
    //  view.setSecurityCode(securityCode, true);
  }

  @ReactProp(name = "numberPlaceholder")
  public void setCreditCardTextHint(CardInputWidget view, String creditCardTextHint) {
    // view.setCardHint(creditCardTextHint);
    // view.setCreditCardTextHint(creditCardTextHint);
  }

  @ReactProp(name = "expirationPlaceholder")
  public void setExpDateTextHint(CardInputWidget view, String expDateTextHint) {
    // view.setCardHint(expDateTextHint);
    // view.setExpDateTextHint(expDateTextHint);
  }

  @ReactProp(name = "cvcPlaceholder")
  public void setSecurityCodeTextHint(CardInputWidget view, String securityCodeTextHint) {
    // view.setCardHint(securityCodeTextHint);
    // view.setSecurityCodeTextHint(securityCodeTextHint);
  }


  private void setListeners(final CardInputWidget view) {

    view.setCardNumberTextWatcher(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: cardNumber = " + charSequence);
        currentNumber = charSequence.toString().replaceAll(" ", "");
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    view.setCvcNumberTextWatcher(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      /*  Log.d(TAG, "onTextChanged: EXP_YEAR = " + charSequence);
        try {
          currentMonth = view.getCard().getExpMonth();
          //  currentMonth = view.getCreditCard().getExpMonth();
        } catch (Exception e) {
          if (charSequence.length() == 0)
            currentMonth = 0;
        }
        try {
          currentYear = view.getCard().getExpYear();
        } catch (Exception e) {
          currentYear = 0;
        }
        postEvent(view);*/
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });

    view.setCvcNumberTextWatcher(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
       /* Log.d(TAG, "onTextChanged: CCV = " + charSequence);
        currentCCV = charSequence.toString();
        postEvent(view);*/
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });

    /*
    final EditText ccNumberEdit = (EditText) view.findViewById(R.id.cc_card);
    final EditText ccExpEdit = (EditText) view.findViewById(R.id.cc_exp);
    final EditText ccCcvEdit = (EditText) view.findViewById(R.id.cc_ccv);

    ccNumberEdit.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: cardNumber = " + charSequence);
        currentNumber = charSequence.toString().replaceAll(" ", "");
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    ccExpEdit.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: EXP_YEAR = " + charSequence);
        try {
          currentMonth = view.getCreditCard().getExpMonth();
        } catch (Exception e) {
          if (charSequence.length() == 0)
            currentMonth = 0;
        }
        try {
          currentYear = view.getCreditCard().getExpYear();
        } catch (Exception e) {
          currentYear = 0;
        }
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    ccCcvEdit.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: CCV = " + charSequence);
        currentCCV = charSequence.toString();
        postEvent(view);
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });*/
  }

  private void postEvent(CardInputWidget view) {
    currentParams = Arguments.createMap();
    currentParams.putString(NUMBER, currentNumber);
    currentParams.putInt(EXP_MONTH, currentMonth);
    currentParams.putInt(EXP_YEAR, currentYear);
    currentParams.putString(CCV, currentCCV);
    reactContext.getNativeModule(UIManagerModule.class)
      .getEventDispatcher();
     /* .dispatchEvent(
        new CreditCardFormOnChangeEvent(
          view.getId(),
          currentParams,
          view.getCard().validateCard())
      );*/
  }

  private void updateView(CardInputWidget view) {

  }
}
