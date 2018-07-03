import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ExampleController {
    @FXML
    TextField inputField;

    @FXML
    Text outputText;

    public void updateOutput(ActionEvent actionEvent) {
        outputText.setText(inputField.getText());
    }
}
