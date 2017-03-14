package eap.adp.plugin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PluginFragment extends Fragment {
    private static final String TAG = "PluginFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plugin_layout, null);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "PluginFragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "PluginFragment onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "PluginFragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "PluginFragment onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "PluginFragment onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "PluginFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "PluginFragment onDetach");
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "PluginFragment onAttach");
        super.onAttach(context);
    }
}
