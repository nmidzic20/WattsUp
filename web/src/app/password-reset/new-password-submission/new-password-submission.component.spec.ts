import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewPasswordSubmissionComponent } from './new-password-submission.component';

describe('NewPasswordSubmissionComponent', () => {
  let component: NewPasswordSubmissionComponent;
  let fixture: ComponentFixture<NewPasswordSubmissionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewPasswordSubmissionComponent]
    });
    fixture = TestBed.createComponent(NewPasswordSubmissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
