### Setup

1. Pick up a flasher, inspect all pins are in place and plugged in, referring to the pin guide.
2. Plug in the flasher to a 5V supply using a microUSB cable.
3. After the green LED is constantly lit and stable, wait another 20-30 seconds.
4. Note the Flasher's ID as written on the back.



### SSH Into the RasPi

1. Open up your preferred terminal application

   - Windows: cmd, powershell
   - Mac: terminal
   - Linux: terminal, termux, terminator

2. SSH into the machine by running the command:
   ```bash
   ssh pi@innexgoflasher<flasher number>
   ```

   - Replace \<flasher number> with the number of the flasher's ID

3. You'll get a response that looks like:

   1. For the first time you connect to a flasher you'll get a message close to:

      ```
      The authenticity of host 'innexgoflasher1 (255.255.255.255)' cannot be established.
      
      RSA key fingerprint is FE:ED:C0:DE:FE:ED:C0:DE:FE:ED:C0:DE:FE:ED:C0:DE.
      Are you sure you want to continue connecting (yes/no)?
      ```

      Please type in `yes` and press the enter key.

      The response should look like: `Warning: Permanently added 'innexgoflasher1' (RSA) to the list of known hosts.`

      After this, follow the next instruction to login

   2. If you've connected to the flasher before, it will immediately prompt for a password.

      `Enter passphrase for key 'pi@innexgoflasher1':`

      The password for each flasher is ***REDACTED***




### Starting the Client

1. Change your directory into rpi-client by running: `cd rpi-flasher/`
2. Update the code to the most current version: `git pull`
3. Start the client: `python3 flasher.py`



### Flashing Stickers to ID

1. After starting the flasher program, wait until it prints: `Please enter student ID...`
2. Enter a student ID, and press the enter key.
3. It should now say: `Please touch card...`
4. Touch a RFID/NFC sticker to the RFID/NFC sensor, and it should say: `detected card with id <id>`
5. If it says: `Write successful` , Continue to the next card and continue flashing stickers.
6. Otherwise, please report the incident and write down how you came across the issue.