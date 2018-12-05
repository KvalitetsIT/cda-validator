import { Component, OnInit } from '@angular/core';
import { ValidationService } from '../../validation.service'

@Component({
  selector: 'app-direct-input',
  templateUrl: './direct-input.component.html',
  styleUrls: ['./direct-input.component.css']
})
export class DirectInputComponent implements OnInit {
  //input
  isValidationDisabled: boolean = false
  cdaTypes: object;
  pickedCDAType: string = "NONE";
  stringToValidate: string = "";

  //output
  errors: any[] = []
  warnings: any[] = []
  infos: any[] = []

  constructor(private validation: ValidationService) { }

  ngOnInit() {
    this.isValidationDisabled = true;
    this.validation.getCDATypes().subscribe(data => {
      this.cdaTypes = data;
      console.log(this.cdaTypes);
    });
  }

  onStringToValidateChange(str: string) {
    this.stringToValidate = str;

    if(this.stringToValidate != '') {
      this.isValidationDisabled = false;
    } else {
      this.isValidationDisabled = true;
    }

    console.log("StringToValidate: " + this.stringToValidate + "; Validation button disabled: " + this.isValidationDisabled);
  }

  onValidationTypeChange(type: string) {
    this.pickedCDAType = type;
    console.log("CDA type picked: " + this.pickedCDAType)
  }

  onClickValidate() {
    console.log("Validation initiated")
    let res = this.validation.validate(this.pickedCDAType, this.stringToValidate)
                  .subscribe(
                    (data: any) => {
                      console.log(data);
                      this.errors = data.errors;
                      this.warnings = data.warnings;
                      this.infos = data.infos;

                      const element = document.querySelector('#validationResults');
                      element.scrollIntoView();
                    },
                    error => console.log(error)
                  );
  }
}