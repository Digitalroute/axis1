/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.axis.tool.codegen.eclipse.pages;

import org.apache.axis.tool.codegen.eclipse.CodegenWizardPlugin;
import org.apache.axis.tool.codegen.eclipse.util.UIConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class OptionsPage extends WizardPage implements UIConstants {
    
    private Combo languageSelectionComboBox;
    private Button syncOnlyRadioButton;
    private Button asyncOnlyRadioButton;
    private Text packageText;
    /**
     * @param pageName
     */
    public OptionsPage() {
        super(CodegenWizardPlugin.getResourceString("page2.name"));
		setTitle(CodegenWizardPlugin.getResourceString("page2.title"));
		setDescription(CodegenWizardPlugin.getResourceString("page2.desc"));
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        
        Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		Label label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin.getResourceString("page2.language.caption"));

		languageSelectionComboBox = new Combo(container, SWT.DROP_DOWN |SWT.BORDER |SWT.READ_ONLY);
		//fill the combo
		this.fillLanguageCombo();
		languageSelectionComboBox.setLayoutData(gd);
		syncOnlyRadioButton = new Button(container,SWT.RADIO);
		syncOnlyRadioButton.setText(CodegenWizardPlugin.getResourceString("page2.sync.caption"));
		
		asyncOnlyRadioButton = new Button(container,SWT.RADIO);
		asyncOnlyRadioButton.setText(CodegenWizardPlugin.getResourceString("page2.async.caption"));
		
		
		label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin.getResourceString("page2.package.caption"));
		packageText = new Text(container,SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		packageText.setLayoutData(gd);
		packageText.setText("default");
		
		setControl(container);
		setPageComplete(true);

    }
    
    private void fillLanguageCombo(){
        
        languageSelectionComboBox.add(JAVA);
        languageSelectionComboBox.add(C_SHARP);
        languageSelectionComboBox.add(C_PLUS_PLUS);
        
        languageSelectionComboBox.setText(languageSelectionComboBox.getItem(0));
    }
    
    /**
     * 
     * @return
     */
    public String getSelectedLanguage(){
        return languageSelectionComboBox.getItem(languageSelectionComboBox.getSelectionIndex());
    }

    public boolean isAsyncOnlyOn(){
        return asyncOnlyRadioButton.getSelection();
    }
    
    public boolean isSyncOnlyOn(){
        return syncOnlyRadioButton.getSelection();
    }
    
    public String getPackageName(){
       return this.packageText.getText();
    }
}
