import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-add-station-dialogue',
  templateUrl: './add-station-dialogue.component.html',
  styleUrls: ['./add-station-dialogue.component.scss']
})
export class AddStationDialogueComponent {
  @Input() isVisible: boolean = false;
  @Output() closeDialogue: EventEmitter<void> = new EventEmitter<void>();

  close() {
    this.isVisible = false;
    this.closeDialogue.emit();
  }
}
