package es.roboticafacil.dyor.tabbed.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.WorkspaceFragment;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import es.roboticafacil.dyor.tabbed.R;
import es.roboticafacil.dyor.tabbed.Activities.Fragments.SelectSaveLocal;

public class BlocklyActivity extends AbstractBlocklyActivity {

    Intent intent;
    String xml;

    String mCode;
    BaseActivity ba;
    WorkspaceFragment mFragmentWorkshop;
    String[] fileList;

    private ProgressDialog mProgressDialog;
    String xmlName;

    SelectSaveLocal mSelectDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ba = new BaseActivity();
        mFragmentWorkshop = new WorkspaceFragment();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return "default/toolbox.xml";
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return Arrays.asList(
                "default/list_blocks.json",
                "default/logic_blocks.json",
                "default/loop_blocks.json",
                "default/math_blocks.json",
                "default/text_blocks.json",
                "default/variable_blocks.json",
                "default/colour_blocks.json"
        );

    }

    private static final List<String> JAVASCRIPT_GENERATORS = Arrays.asList(
            // Custom block generators go here. Default blocks are already included.

    );
    private Handler mHandler=new Handler();

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

  //  CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
   //         new LoggingCodeGeneratorCallback(this, "code");

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCode = generatedCode;

                            Toast.makeText(BlocklyActivity.this, mCode, Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference("/saves/").push().setValue("Testing DB" + mCode).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                }
                            });


                        }
                    });
                }
            };


    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {


        return mCodeGeneratorCallback;
    }

    @Override
    protected void onInitBlankWorkspace() {

        intent = getIntent();
        xml = intent.getStringExtra("xml");
        if (xml.equals("null")) {
            super.onInitBlankWorkspace();
            xmlName = "null";
        } else {
            mBlockly.loadWorkspaceFromAppDirSafely(xml);
            xmlName = xml;
        }
        Log.e("File", "Setting file name");

    }


    @Override
    protected void onRunCode() {
        super.onRunCode();
    }

    @Override
    public void onSaveWorkspace() {

//        //showProgressDialog();
//        //mBlockly.requestCodeGeneration(this.getBlockDefinitionsJsonPaths(),this.getGeneratorsJsPaths(),this.getCodeGenerationCallback());
//        DialogFragment saveChoiceDialog = new SelectSaveTypeDialogFragment();
//        Bundle args = new Bundle();
//        args.putString("code", mCode);
//
//
//
//        saveChoiceDialog.setArguments(args);
//        saveChoiceDialog.show(getSupportFragmentManager(), "Save Dialog");
        if (fileExists(xmlName) && !xmlName.equals("")){
            mBlockly.saveWorkspaceToAppDirSafely(xmlName);
        }
        else {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");

            LayoutInflater inflator = getLayoutInflater();
            final View viewInflated = inflator.inflate(R.layout.create_dialog, null);

            builder.setView(viewInflated)
                    .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText etXmlName = (EditText) viewInflated.findViewById(R.id.et_name);
                            String name = etXmlName.getText().toString();
                            xmlName = name + ".xml";
                            mBlockly.saveWorkspaceToAppDirSafely(xmlName);
                            Log.e("File", "Saved as: " + xmlName);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.show();
        }
        //super.onSaveWorkspace();
    }

    @Override
    public void onLoadWorkspace() {
        //mSelectDialog = new SelectSaveLocal();
        //Bundle args = new Bundle();
        //args.putStringArray("list", fileList());
        //mSelectDialog.setArguments(args);
        //mSelectDialog.show(getSupportFragmentManager(), "Dialog");


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title")
                .setItems(fileList(), new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         String[] list = fileList();
                         mBlockly.loadWorkspaceFromAppDirSafely(list[which]);

                 }
        });
        builder.show();

        //super.onLoadWorkspace();
    }

    public void loadLocalFile(int i){
        fileList = fileList();
        Log.e("File", "Loading list file");
        mBlockly.loadWorkspaceFromAppDirSafely(fileList[i]);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);

            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void uploadCode(){
        mBlockly.requestCodeGeneration(this.getBlockDefinitionsJsonPaths(),this.getGeneratorsJsPaths(),this.getCodeGenerationCallback());
    }

    public boolean fileExists(String filename) {
        File f = new File(filename + ".xml");
        if(f.isFile() && f.canRead()){
            return true;
        }
        else return false;

    }

    public String getReturn(String s){
        return s;
    }


}
