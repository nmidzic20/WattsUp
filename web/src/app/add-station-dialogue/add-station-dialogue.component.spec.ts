import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddStationDialogueComponent } from './add-station-dialogue.component';

describe('AddStationDialogueComponent', () => {
  let component: AddStationDialogueComponent;
  let fixture: ComponentFixture<AddStationDialogueComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddStationDialogueComponent]
    });
    fixture = TestBed.createComponent(AddStationDialogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
