package comp3350.plarty.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.Button;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import comp3350.plarty.R;
import comp3350.plarty.application.Main;

/**
 * The title screen of Plarty!
 */
@RequiresApi(Build.VERSION_CODES.M)
public class MainActivity extends Activity {
    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyDatabaseToDevice();
        Main.startUp();
        setContentView(R.layout.activity_main);

        Button viewUserProfile = findViewById(R.id.view_profile_button);
        viewUserProfile.setOnClickListener(view -> navigate(UserProfileActivity.class));

        Button viewCalendar = findViewById(R.id.view_schedule_button);
        viewCalendar.setOnClickListener(view -> navigate(MonthlyScheduleActivity.class));

        Button viewEvents = findViewById(R.id.view_events_button);
        viewEvents.setOnClickListener(view -> navigate(ViewEventsActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Main.shutDown();
    }

    private void navigate(Class<?> nextActivity) {
        Intent navigate = new Intent(this, nextActivity);
        startActivity(navigate);
    }

    private void copyDatabaseToDevice() {
        final String DB_PATH = "db";

        String[] assetNames;
        Context context = getApplicationContext();
        File dataDirectory = context.getDir(DB_PATH, Context.MODE_PRIVATE);
        AssetManager assetManager = getAssets();

        try {
            assetNames = assetManager.list(DB_PATH);
            for (int i = 0; i < assetNames.length; i++) {
                assetNames[i] = DB_PATH + "/" + assetNames[i];
            }

            copyAssetsToDirectory(assetNames, dataDirectory);

            Main.setDBPathName(dataDirectory.toString() + "/" + Main.dbName);

        } catch (IOException ioe) {
            System.out.println("Unable to access application data: " + ioe.getMessage());
        }
    }
    public void copyAssetsToDirectory(String[] assets, File directory) throws IOException {
        AssetManager assetManager = getAssets();

        for (String asset : assets) {
            String[] components = asset.split("/");
            String copyPath = directory.toString() + "/" + components[components.length - 1];
            char[] buffer = new char[1024];
            int count;

            File outFile = new File(copyPath);

            if (!outFile.exists()) {
                InputStreamReader in = new InputStreamReader(assetManager.open(asset));
                FileWriter out = new FileWriter(outFile);

                count = in.read(buffer);
                while (count != -1) {
                    out.write(buffer, 0, count);
                    count = in.read(buffer);
                }

                out.close();
                in.close();
            }
        }
    }
}
