/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translator;

import conn.ConnectionClass;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author Raj
 */
public class MainController implements Initializable {

    @FXML
    public TextField inputWord;
    public String value = "";
    public boolean start = true;
    public TextArea textShow;
    public String add = "";
    public String checker = "";
    public String locate = "";
    public String speakWord = "";
    public Label label1;
    public ImageView newImg;
    public Button b;
    public int count = 0;
    public String musicFile="";
    Set<String> words = new HashSet<>();
    Set<String> updatedWords = new HashSet<>();
    SuggestionProvider<String> provider = SuggestionProvider.create(words);
// and after some times, possible autoCompletions values has changed and now we have:
    public AutoCompletionBinding auto;
    
    // MediaPlayer mediaPlayer=  new MediaPlayer();
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //display speaker image from img package..
        Image speakerImage = new Image("img/speaker.png");
        ImageView imageView = new ImageView(speakerImage);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        b.setGraphic(imageView);
        String sql = "Select sindhiWrd from sindhitoenglish";
        try ( Connection connect = ConnectionClass.getCon();  Statement st = connect.createStatement();  ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                words.add(rs.getString("sindhiWrd"));
                break;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        auto = TextFields.bindAutoCompletion(inputWord, provider);
        //new AutoCompletionTextFieldBinding<>(inputWord, provider);
        inputWord.setOnKeyReleased((KeyEvent e) -> {
        if (e.getCode() == KeyCode.BACK_SPACE) {
            try {
                createSet();
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
        
        });
    }

    public void speak(ActionEvent event) throws SQLException {
        speakWord = inputWord.getText();
        //method call to save word to word set for autocompletion.
        Connection connection = ConnectionClass.getCon();
        String sql = "SELECT audiolocation FROM sindhitoenglish WHERE sindhiWrd=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, speakWord);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            locate = rs.getString("audiolocation");
            break;
        }
        try {
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //audio files are stored in audio package inside the Source package...
        // locate audio file and play it....
        musicFile = locate;
        Media sound = new Media(new File(musicFile).toURI().toString());
        
        MediaPlayer mediaPlayer = new MediaPlayer(sound); 
        
        
        mediaPlayer.play();
        locate = "";
        
    /*try {
        mediaPlayer.setDataSource(musicFile);
        mediaPlayer.prepare();
    } catch (IOException e) {
        e.printStackTrace();
    }
    mediaPlayer.start();
      */  

    }

    public void processText(ActionEvent event) throws SQLException {

        if (start) {
            inputWord.setText("");
            start = false;
        }
        Button button = (Button) event.getSource();
        inputWord.replaceSelection(button.getText());
        createSet();

    }

    //Search query
    public void saveText(ActionEvent event) throws SQLException {
        textShow.clear();
        value = inputWord.getText();

        //method call to save word to word set for autocompletion.
        Connection connection = ConnectionClass.getCon();
        String sql = "SELECT englishWrd FROM sindhitoenglish WHERE sindhiWrd=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, value);
        ResultSet rs = pstmt.executeQuery();

        //print result.
        while (rs.next()) {
            textShow.setText("Result for " + value + "\n" + rs.getString("englishWrd"));
            //textShow.setText("Result for " + value + "\n" + rs.getString("sindhiWords"));
            break;
        }
        try {
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //Print error message if word not found.....
        checker = textShow.getText();
        if (value.length() > 0 && checker.length() == 0) {
            textShow.setText("Could not find the word...");
        }

        value = "";
    }

    //Creates dropdown list.....
    public void createSet() throws SQLException {
        updatedWords.clear();
        add = "";
        count=0;
        add = inputWord.getText();
        String sqlGet = "Select sindhiWrd from sindhitoenglish where sindhiWrd like ?";

        Connection connect = ConnectionClass.getCon();
        PreparedStatement pt = connect.prepareStatement(sqlGet);
        pt.setString(1, add + "%");
        ResultSet rst = pt.executeQuery();
        while (rst.next()) {
            count++;
            updatedWords.add(rst.getString("sindhiWrd"));
            if (count == 10) {
                break;
            }
        }

        try {
            rst.close();
            pt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        provider.clearSuggestions();
        provider.addPossibleSuggestions(updatedWords);

    }

    //Contact us window...
    public void aboutUs(ActionEvent event) throws Exception {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/translator/contact.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Contact us");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

}
