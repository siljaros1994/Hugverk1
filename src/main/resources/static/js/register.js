document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.getElementById('submit');
    if (submitButton) {
        submitButton.addEventListener('click', async () => {
            const username = document.getElementById('register__username').value.trim();
            const email = document.getElementById('register__email').value;
            const password = document.getElementById('register__password').value;
            const confirmPassword = document.getElementById('register__confirmPassword').value;
            const donorRecipient = document.getElementById('donorRecipient').value.trim().toLowerCase();


            // Check if passwords match
            if (password !== confirmPassword) {
                alert('Passwords do not match');
                return;
            }

            // Create the user data object
            let userData = {
                username: username,
                email: email,
                password: password,
                confirmPassword: confirmPassword,  // Ensure both are sent for validation
                userType: donorRecipient
            };

            // Set the registration endpoint
            let registerUrl = 'http://localhost:8081/users/register';

            try {
                // Make the POST request
                const response = await fetch(registerUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify(userData)
                });

                // Check for response status
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error(errorText);  // Log error for troubleshooting
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }

                // If successful, display success message
                const data = await response.json();
                alert('User registered successfully!');
            } catch (error) {
                // Display any error that occurs
                console.error(error.message);
                alert(`Registration failed: ${error.message}`);
            }
        });
    } else {
        console.error("Submit button not found!");
    }
});
