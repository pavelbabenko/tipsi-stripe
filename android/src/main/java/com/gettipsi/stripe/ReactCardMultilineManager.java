package com.gettipsi.stripe;

import android.view.View;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.stripe.android.view.CardMultilineWidget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactCardMultilineManager extends BaseViewManager {
  public static final String REACT_CLASS = "TPSCardField";

  @Nonnull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @ReactProp(name = "enabled", defaultBoolean = true)
  public void setEnabled(ReactCardMultilineView view, boolean enabled) {
    view.setEnabledFromJS(enabled);
  }

  @ReactProp(name = "postalCodeEntryEnabled", defaultBoolean = true)
  public void setPostalCodeEntryEnabled( ReactCardMultilineView view, boolean enabled ) {
    view.setPostalCodeEnabledFromJS(enabled);
  }

  @Override
  public Class getShadowNodeClass() {
    return ReactCardMultilineShadowNode.class;
  }

  @Override
  public ReactCardMultilineShadowNode createShadowNodeInstance(){
    return new ReactCardMultilineShadowNode();
  }

  @Nonnull
  @Override
  protected View createViewInstance(@Nonnull ThemedReactContext reactContext) {
    return new ReactCardMultilineView(reactContext);
  }

  @Override
  public void updateExtraData(@Nonnull View root, Object extraData) {
      // do nothing
  }
}
