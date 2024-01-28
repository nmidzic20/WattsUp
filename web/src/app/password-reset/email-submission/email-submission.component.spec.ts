import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailSubmissionComponent } from './email-submission.component';

describe('EmailSubmissionComponent', () => {
  let component: EmailSubmissionComponent;
  let fixture: ComponentFixture<EmailSubmissionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmailSubmissionComponent]
    });
    fixture = TestBed.createComponent(EmailSubmissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
