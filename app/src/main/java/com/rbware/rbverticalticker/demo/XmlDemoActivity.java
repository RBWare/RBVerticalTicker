package com.rbware.rbverticalticker.demo;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.rbware.rbverticalticker.VerticalTickerView;
import java.util.Arrays;
import java.util.List;

/** Demonstrates driving a {@link VerticalTickerView} from an XML layout and plain Java. */
public class XmlDemoActivity extends AppCompatActivity {

    private static final List<String> HEADLINES = Arrays.asList(
            "RBVerticalTicker is live",
            "Swipe-free, auto-advancing headlines",
            "Built with Jetpack Compose",
            "Drop it into any screen"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_demo);

        VerticalTickerView ticker = findViewById(R.id.ticker);
        ticker.setItems(HEADLINES);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> ticker.showNext());

        Button injectButton = findViewById(R.id.injectButton);
        injectButton.setOnClickListener(v -> ticker.showNext("Breaking: manual entry injected!"));
    }
}
