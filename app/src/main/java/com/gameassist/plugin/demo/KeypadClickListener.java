package com.gameassist.plugin.demo;
import android.view.View;
import android.widget.Toast;


import com.gameassist.plugin.mod.demo.R;

public class KeypadClickListener implements View.OnClickListener {
    PluginManagerView pluginManagerView;
    public KeypadClickListener(PluginManagerView view) {
        this.pluginManagerView = view;
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.main_play:
                Toast.makeText(v.getContext(), "Hello World !", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
