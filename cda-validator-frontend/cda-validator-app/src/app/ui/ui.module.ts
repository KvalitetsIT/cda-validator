import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout/layout.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { DirectInputComponent } from './direct-input/direct-input.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { ResultsComponent } from './results/results.component';

@NgModule({
  declarations: [LayoutComponent, HeaderComponent, FooterComponent, DirectInputComponent, FileUploadComponent, ResultsComponent],
  imports: [
    CommonModule
  ],
  exports: [LayoutComponent]
})
export class UiModule { }