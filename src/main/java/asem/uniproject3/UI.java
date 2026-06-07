package asem.uniproject3;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

public class UI {

    private final Converter converter;
    private final Conventional conventional;
    private final Custom custom;
    private final FileHandler fileHandler;
    private CStack<String> loadedExpressions;

    public UI() {
        this.converter = new Converter();
        this.conventional = new Conventional();
        this.fileHandler = new FileHandler();
        CursorArray<Operator> ops = new CursorArray<>(200);
        CursorArray<Character> dom = new CursorArray<>(200);
        this.custom = new Custom(ops, dom);
        this.loadedExpressions = null;
    }

    public Scene createScene(Stage stage) {

        RadioButton rbCustom = new RadioButton("Custom Notation");
        RadioButton rbConventional = new RadioButton("Conventional Notation");
        ToggleGroup modeGroup = new ToggleGroup();
        rbCustom.setToggleGroup(modeGroup);
        rbConventional.setToggleGroup(modeGroup);
        rbCustom.setSelected(true);

        ChoiceBox<String> choiceNotation = new ChoiceBox<>();
        choiceNotation.getItems().addAll("Infix", "Postfix", "Prefix");
        choiceNotation.setValue("Infix");

        TextField txtExpression = new TextField();
        txtExpression.setPromptText("Enter expression here");

        TextArea txtInfix = new TextArea();
        TextArea txtPostfix = new TextArea();
        TextArea txtPrefix = new TextArea();
        TextArea txtEvaluation = new TextArea();
        txtInfix.setWrapText(true);
        txtPostfix.setWrapText(true);
        txtPrefix.setWrapText(true);
        txtEvaluation.setWrapText(true);

        Button btnConvert = new Button("Convert");
        Button btnClear = new Button("Clear");
        Button btnLoadDomain = new Button("Load Language Domain");
        Button btnLoadPrecedence = new Button("Load Precedence Rules");
        Button btnGenerateReport = new Button("Generate Report");

        Button btnLoadExpressions = new Button("Load Expressions");
        Button btnNextExpression = new Button("Next Expression");

        Button editDomainBtn = new Button("Edit Domain");
        Button editPrecedenceBtn = new Button("Edit Precedence");

        btnNextExpression.setDisable(true);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: green;");

        HBox modeBox = new HBox(10, new Label("Mode:"), rbCustom, rbConventional);
        HBox notationBox = new HBox(10, new Label("Input Notation:"), choiceNotation);
        HBox buttons = new HBox(10, btnConvert, btnClear);

        HBox loadButtons = new HBox(10, btnLoadDomain, editDomainBtn, btnLoadPrecedence, editPrecedenceBtn, btnLoadExpressions, btnNextExpression, btnGenerateReport);

        GridPane results = new GridPane();
        results.setHgap(10);
        results.setVgap(10);
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        results.getColumnConstraints().addAll(col, col);

        results.add(new Label("Infix:"), 0, 0);
        results.add(new Label("Postfix:"), 1, 0);
        results.add(txtInfix, 0, 1);
        results.add(txtPostfix, 1, 1);
        results.add(new Label("Prefix:"), 0, 2);
        results.add(new Label("Evaluation:"), 1, 2);
        results.add(txtPrefix, 0, 3);
        results.add(txtEvaluation, 1, 3);

        VBox root = new VBox(10, modeBox, notationBox, new Label("Expression:"), txtExpression, buttons, loadButtons, new Label("Results:"), results, lblError);
        root.setPadding(new Insets(10));

        Runnable updateMode = () -> {
            lblError.setText("");
        };

        rbCustom.setOnAction(e -> updateMode.run());
        rbConventional.setOnAction(e -> updateMode.run());

        btnLoadDomain.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open Language Domain File");
            File f = chooser.showOpenDialog(stage);
            if (f != null) {
                try {
                    int added = fileHandler.domainReader(f, custom);
                    lblError.setStyle("-fx-text-fill: green;");
                    if (added > 0) {
                        lblError.setText("Language domain loaded (" + added + " items): " + f.getName());
                    } else {
                        lblError.setText("Language domain file parsed but no operands found: " + f.getName());
                    }
                } catch (Exception ex) {
                    lblError.setStyle("-fx-text-fill: red;");
                    lblError.setText("Failed to load domain: " + ex.getMessage());
                }
            }
        });

        btnLoadPrecedence.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open Precedence Rules File");
            File f = chooser.showOpenDialog(stage);
            if (f != null) {
                try {
                    int added = fileHandler.operatorReader(f, custom);
                    lblError.setStyle("-fx-text-fill: green;");
                    if (added > 0) {
                        lblError.setText("Precedence rules loaded (" + added + " operators): " + f.getName());
                    } else {
                        lblError.setText("Precedence file parsed but no operators found: " + f.getName());
                    }
                } catch (Exception ex) {
                    lblError.setStyle("-fx-text-fill: red;");
                    lblError.setText("Failed to load precedence: " + ex.getMessage());
                }
            }
        });

        editDomainBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Domain File to Edit");
            File file = fc.showOpenDialog(stage);

            if (file == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "No File Selected", "Please select a file that you want to edit.");
                return;
            }

            TextArea textAreaForEditDomain = new TextArea();
            textAreaForEditDomain.setWrapText(true);
            Button saveBtn = new Button("Save Changes");

            try (Scanner in = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (in.hasNextLine()) {
                    content.append(in.nextLine()).append("\n");
                }
                textAreaForEditDomain.setText(content.toString());
            } catch (FileNotFoundException e1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Read File", e1.getMessage());
                return;
            }

            VBox vNewStage1 = new VBox(10);
            vNewStage1.setPadding(new Insets(10));
            vNewStage1.setAlignment(Pos.CENTER);
            vNewStage1.getChildren().addAll(textAreaForEditDomain, saveBtn);

            Stage ss = new Stage();
            ss.setTitle("Edit Domain File - " + file.getName());
            Scene scene = new Scene(vNewStage1, 500, 400);
            ss.setResizable(false);
            ss.setScene(scene);
            ss.show();

            saveBtn.setOnAction(e2 -> {
                try (PrintWriter out = new PrintWriter(file)) {
                    out.print(textAreaForEditDomain.getText().trim());
                    out.flush();

                    custom.getLanguageDomain().clear(custom.getListDo());
                    int added = fileHandler.domainReader(file, custom);

                    ss.close();
                    lblError.setStyle("-fx-text-fill: green;");
                    lblError.setText("Domain reloaded with " + added + " tokens.");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "File Saved", "File updated and reloaded into system successfully.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Save Failed", ex.getMessage());
                }
            });
        });

        editPrecedenceBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Precedence Rules File to Edit");
            File file = fc.showOpenDialog(stage);

            if (file == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "No File Selected", "Please select a precedence rules file that you want to edit.");
                return;
            }

            TextArea textAreaForEditPrecedence = new TextArea();
            textAreaForEditPrecedence.setWrapText(true);
            Button saveBtn = new Button("Save Changes");

            try (Scanner in = new Scanner(file)) {
                StringBuilder content = new StringBuilder();
                while (in.hasNextLine()) {
                    content.append(in.nextLine()).append("\n");
                }
                textAreaForEditPrecedence.setText(content.toString());
            } catch (FileNotFoundException e1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Read File", e1.getMessage());
                return;
            }

            VBox vNewStage2 = new VBox(10);
            vNewStage2.setPadding(new Insets(10));
            vNewStage2.setAlignment(Pos.CENTER);
            vNewStage2.getChildren().addAll(textAreaForEditPrecedence, saveBtn);

            Stage ss = new Stage();
            ss.setTitle("Edit Precedence File - " + file.getName());
            Scene scene = new Scene(vNewStage2, 500, 400);
            ss.setResizable(false);
            ss.setScene(scene);
            ss.show();

            saveBtn.setOnAction(e2 -> {
                try (PrintWriter out = new PrintWriter(file)) {
                    String inputData = textAreaForEditPrecedence.getText().trim();

                    if (!inputData.toLowerCase().startsWith("operators")) {
                        out.println("Operators Priority");
                    }
                    out.print(inputData);
                    out.flush();

                    custom.getOperator().clear(custom.getListOp());
                    int added = fileHandler.operatorReader(file, custom);

                    ss.close();
                    lblError.setStyle("-fx-text-fill: green;");
                    lblError.setText("Operators reloaded with " + added + " tokens.");
                    showAlert(Alert.AlertType.INFORMATION, "Success", "File Saved", "Precedence file updated and reloaded into system successfully.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Save Failed", ex.getMessage());
                }
            });
        });

        btnLoadExpressions.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open Expressions File");
            File f = chooser.showOpenDialog(stage);
            if (f != null) {
                try {
                    this.loadedExpressions = fileHandler.expReader(f);
                    lblError.setStyle("-fx-text-fill: green;");
                    if (this.loadedExpressions != null && !this.loadedExpressions.isEmpty()) {
                        btnNextExpression.setDisable(false);
                        int skipped = fileHandler.getLastExpReaderSkipped();
                        lblError.setText("Loaded expressions file: " + f.getName() + " (Skipped invalid lines: " + skipped + "). Click 'Next Expression' to iterate.");
                    } else {
                        btnNextExpression.setDisable(true);
                        lblError.setText("Expressions file was empty or valid expressions were not found.");
                    }
                } catch (Exception ex) {
                    lblError.setStyle("-fx-text-fill: red;");
                    lblError.setText("Failed to load expressions: " + ex.getMessage());
                }
            }
        });

        btnNextExpression.setOnAction(e -> {
            if (loadedExpressions == null || loadedExpressions.isEmpty()) {
                lblError.setStyle("-fx-text-fill: red;");
                lblError.setText("No more expressions left in the file.");
                btnNextExpression.setDisable(true);
                return;
            }
            String nextExpr = loadedExpressions.pop();
            String[] temp = nextExpr.split("\\|");
            if (temp[0].equalsIgnoreCase("infix")){
                choiceNotation.setValue("Infix");
            } else if (temp[0].equalsIgnoreCase("postfix")){
                choiceNotation.setValue("Postfix");
            } else if (temp[0].equalsIgnoreCase("prefix")){
                choiceNotation.setValue("Prefix");
            }

            txtExpression.setText(temp[1].trim());
            btnConvert.fire();

            lblError.setStyle("-fx-text-fill: green;");
            if (loadedExpressions.isEmpty()) {
                lblError.setText("Processed last expression from the file.");
                btnNextExpression.setDisable(true);
            } else {
                lblError.setText("Displaying next expression from file.");
            }
        });


        btnConvert.setOnAction(e -> {
            txtInfix.clear();
            txtPostfix.clear();
            txtPrefix.clear();
            txtEvaluation.clear();

            String expr = Optional.ofNullable(txtExpression.getText()).orElse("").trim();
            if (expr.isEmpty()) {
                lblError.setStyle("-fx-text-fill: red;");
                lblError.setText("Error: Expression cannot be empty");
                return;
            }

            boolean customMode = rbCustom.isSelected();
            OperatorRules rules = customMode ? custom : conventional;
            String notation = choiceNotation.getValue();

            if (customMode) {
                if (custom.getLanguageDomain().isEmpty(custom.getListDo()) || custom.getOperator().isEmpty(custom.getListOp())) {
                    lblError.setStyle("-fx-text-fill: red;");
                    lblError.setText("Custom mode: please load language domain and precedence rules before converting.");
                    return;
                }
            }

            String validationError = converter.validate(expr, rules, notation);
            if (validationError != null) {
                lblError.setStyle("-fx-text-fill: red;");
                lblError.setText(validationError);
                return;
            }

            lblError.setText("");

            try {
                switch (notation.toLowerCase()) {
                    case "infix":
                        String post = converter.infixToPostfix(expr, rules);
                        String pref = converter.infixToPrefix(expr, rules);
                        txtInfix.setText(expr);
                        txtPostfix.setText(post == null ? "" : post);
                        txtPrefix.setText(pref == null ? "" : pref);
                        if (!customMode) {
                            try {
                                double val = converter.evaluatePostfix(post == null ? "" : post);
                                txtEvaluation.setText(String.valueOf(val));
                            } catch (Exception ex) {
                                txtEvaluation.setText("Evaluation error: " + ex.getMessage());
                            }
                        }
                        break;
                    case "postfix":
                        String inf = converter.postfixToInfix(expr, rules);
                        if (inf == null) {
                            lblError.setStyle("-fx-text-fill: red;");
                            lblError.setText("Invalid postfix expression");
                            return;
                        }
                        String pref2 = converter.infixToPrefix(inf, rules);
                        txtInfix.setText(inf);
                        txtPrefix.setText(pref2 == null ? "" : pref2);
                        txtPostfix.setText(expr);
                        break;
                    case "prefix":
                        String inf2 = converter.prefixToInfix(expr, rules);
                        if (inf2 == null) {
                            lblError.setStyle("-fx-text-fill: red;");
                            lblError.setText("Invalid prefix expression");
                            return;
                        }
                        String post2 = converter.infixToPostfix(inf2, rules);
                        txtInfix.setText(inf2);
                        txtPostfix.setText(post2 == null ? "" : post2);
                        txtPrefix.setText(expr);
                        if (!customMode) {
                            try {
                                double val = converter.evaluatePostfix(post2 == null ? "" : post2);
                                txtEvaluation.setText(String.valueOf(val));
                            } catch (Exception ex) {
                                txtEvaluation.setText("Evaluation error: " + ex.getMessage());
                            }
                        }
                        break;
                    default:
                        lblError.setStyle("-fx-text-fill: red;");
                        lblError.setText("Unknown notation");
                }
            } catch (Exception ex) {
                lblError.setStyle("-fx-text-fill: red;");
                lblError.setText("Conversion error: " + ex.getMessage());
            }
        });

        btnClear.setOnAction(e -> {
            txtExpression.clear();
            txtInfix.clear();
            txtPostfix.clear();
            txtPrefix.clear();
            txtEvaluation.clear();
            lblError.setText("");
        });

        btnGenerateReport.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Report");
            chooser.setInitialFileName("report.txt");
            File f = chooser.showSaveDialog(stage);
            if (f == null) return;

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
                writer.write("Original: " + Optional.ofNullable(txtExpression.getText()).orElse("") + System.lineSeparator());
                writer.write("Infix: " + Optional.ofNullable(txtInfix.getText()).orElse("") + System.lineSeparator());
                writer.write("Postfix: " + Optional.ofNullable(txtPostfix.getText()).orElse("") + System.lineSeparator());
                writer.write("Prefix: " + Optional.ofNullable(txtPrefix.getText()).orElse("") + System.lineSeparator());
                writer.write("Evaluation: " + Optional.ofNullable(txtEvaluation.getText()).orElse("") + System.lineSeparator());
                writer.flush();
                lblError.setStyle("-fx-text-fill: green;");
                lblError.setText("Report saved: " + f.getAbsolutePath());
            } catch (IOException ex) {
                lblError.setStyle("-fx-text-fill: red;");
                lblError.setText("Failed to write report: " + ex.getMessage());
            }
        });

        return new Scene(root, 1000, 600);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}