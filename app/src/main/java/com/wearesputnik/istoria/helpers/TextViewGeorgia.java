package com.wearesputnik.istoria.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wearesputnik.istoria.R;

/**
 * Created by admin on 23.06.17.
 */
public class TextViewGeorgia extends TextView {

    public TextViewGeorgia(Context context) {
        this(context, null, 0);
    }

    public TextViewGeorgia(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public TextViewGeorgia(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont(context);
    }

    private void setFont(Context context) {
        Typeface face = Typefaces.get(context, context.getText(R.string.font_georgia).toString());
        setTypeface(face);
    }
}
