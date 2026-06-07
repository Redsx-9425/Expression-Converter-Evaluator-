module asem.uniproject3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens asem.uniproject3 to javafx.fxml;
    exports asem.uniproject3;
}