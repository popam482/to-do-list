package application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class Controller {

    @FXML private Button addButton, deleteButton, saveButton;
    @FXML private TextField typeTask;
    @FXML private ListView<Task> todolist;
    @FXML private CheckBox autosaveBox;

    private ContextMenu menu;
    private MenuItem deleteItem, editItem, toggleItem, deadlineItem, sortItem, filterItem, showItem;

    private ObservableList<Task> masterList = FXCollections.observableArrayList();
    private FilteredList<Task> filteredList;
    private SortedList<Task> sortedList;

    private boolean changesMade = false;
    private boolean autosaving = false;
    private boolean filtered = false;
    private boolean sorted = false;
    
    private User currentUser;

    
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadTasks(user);
    }


    @FXML
    public void initialize() {
        filteredList = new FilteredList<>(masterList, t -> true);
        sortedList = new SortedList<>(filteredList);
        todolist.setItems(sortedList);

        setupCell();
        setupContextMenu();
        alertWindow();
        handleClicks();
    }

    private void setupContextMenu() {
        menu = new ContextMenu();

        deleteItem = new MenuItem("Delete current task");
        deleteItem.setOnAction(e -> deleteTask());

        editItem = new MenuItem("Edit current task");
        editItem.setOnAction(e -> editTask());

        toggleItem = new MenuItem("Check/Uncheck current task");
        toggleItem.setOnAction(e -> toggleTask());

        deadlineItem = new MenuItem("Set deadline for the current task");
        deadlineItem.setOnAction(e -> setDeadline());

        sortItem = new MenuItem("Sort tasks by deadline");
        sortItem.setOnAction(e -> sortTasks());

        filterItem = new MenuItem("Show incompleted tasks");
        filterItem.setOnAction(e -> filterTasks());

        showItem = new MenuItem("Show all tasks");
        showItem.setOnAction(e -> showAll());

        menu.getItems().addAll(deleteItem, editItem, toggleItem, deadlineItem, sortItem, filterItem, showItem);
        todolist.setContextMenu(menu);
    }

    private void setupCell() {
        todolist.setCellFactory(lv -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText((t.isCompleted() ? "✔ " : "")
                        + t.getName()
                        + (t.getDeadline() != null && !t.getDeadline().isEmpty()
                        ? " (due: " + t.getDeadline() + ")" : ""));

                if (t.getDeadline() != null && !t.getDeadline().isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate deadlineDate = LocalDate.parse(t.getDeadline(), formatter);
                    int daysLeft = (int) ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate);

                    if (!t.isCompleted()) {
                        if (daysLeft > 7) setStyle("-fx-background-color: lightgreen;");
                        else if (daysLeft > 5) setStyle("-fx-background-color: yellow;");
                        else if (daysLeft > 3) setStyle("-fx-background-color: orange;");
                        else setStyle("-fx-background-color: red;");
                    } else {
                        setStyle("");
                    }
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void alertWindow() {
        todolist.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        stage.setOnCloseRequest(event -> {
                            if (changesMade && !autosaving) {
                                event.consume();
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Exit");
                                alert.setHeaderText("You are about to exit without saving changes");
                                alert.setContentText("Do you want to save the changes?");

                                alert.showAndWait().ifPresent(response -> {
                                    if (response == ButtonType.OK) saveTasks();
                                    stage.close();
                                });
                            } else {
                                saveTasks();
                                stage.close();
                            }
                        });
                    }
                });
            }
        });
    }

    private void handleClicks() {
        todolist.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) toggleTask();
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) deleteTask();
        });
    }

    @FXML
    protected void autosave() {
        autosaving = autosaveBox.isSelected();
    }

    @FXML
    protected void addTask() {
        String taskName = typeTask.getText();
        if (!taskName.isEmpty()) {
            Task t = new Task(taskName, "", false);
            masterList.add(t);
            typeTask.clear();
            changesMade = true;
        }
    }

    @FXML
    protected void addEnter() {
        addTask();
    }

    @FXML
    protected void deleteTask() {
        Task selectedTask = todolist.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            masterList.remove(selectedTask);
            changesMade = true;
        }
    }

    @FXML
    protected void toggleTask() {
        Task selectedTask = todolist.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setCompleted(!selectedTask.isCompleted());
            todolist.refresh();
            changesMade = true;
        }
    }

    protected void showAll() {
        filteredList.setPredicate(t -> true);
        sorted = false;
        filtered = false;
    }

    protected void sortTasks() {
        filteredList.setPredicate(t -> true); 
        sortedList.setComparator((t1, t2) -> {
            if ((t1.getDeadline() == null || t1.getDeadline().isEmpty())
                    && (t2.getDeadline() == null || t2.getDeadline().isEmpty())) return 0;
            if (t1.getDeadline() == null || t1.getDeadline().isEmpty()) return 1;
            if (t2.getDeadline() == null || t2.getDeadline().isEmpty()) return -1;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate d1 = LocalDate.parse(t1.getDeadline(), formatter);
            LocalDate d2 = LocalDate.parse(t2.getDeadline(), formatter);
            return d1.compareTo(d2);
        });
        sorted = true;
    }

    protected void filterTasks() {
        filteredList.setPredicate(t -> !t.isCompleted());
        filtered = true;
    }

    protected void setDeadline() {
        Task selectedTask = todolist.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            TextInputDialog dialog = new TextInputDialog(selectedTask.getDeadline());
            dialog.setTitle("Deadline");
            dialog.setHeaderText("Enter deadline (dd.MM.yyyy)");
            Optional<String> deadline = dialog.showAndWait();
            deadline.ifPresent(date -> {
                selectedTask.setDeadline(date);
                todolist.refresh();
                changesMade = true;
            });
            sortTasks();
        }
    }

    protected void editTask() {
        Task selectedTask = todolist.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            TextInputDialog dialog = new TextInputDialog(selectedTask.getName());
            dialog.setTitle("Edit task");
            dialog.setHeaderText("Enter the new task");
            Optional<String> newTask = dialog.showAndWait();

            newTask.ifPresent(text -> {
                selectedTask.setName(text);
                todolist.refresh();
                changesMade = true;
            });
        }
    }

    protected void loadTasks(User user) {
        if(user != null) {
            this.currentUser = user;
            TaskFileManager tfm = new TaskFileManager();
            tfm.loadFile(masterList, currentUser.getUsername());
        }
    }

    @FXML
    protected void saveTasks() {
        if(currentUser != null) {
            TaskFileManager tfm = new TaskFileManager();
            tfm.saveFile(masterList, currentUser.getUsername());
            changesMade = false;
        }
    }

}
