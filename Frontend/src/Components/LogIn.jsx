import React, { useEffect, useState } from 'react';
import { Form, FormFeedback, FormGroup, Input } from 'reactstrap';
import { toast } from 'react-toastify';
import Base from './Base';
import LogInButton from './LogInButton';
import { register } from '../services/User-service';
import './login.css';

const EMPTY_USER = {
    name: '',
    email: '',
    password: '',
    contactNo: '',
    address: ''
};

const FIELD_NAMES = ['name', 'email', 'password', 'contactNo', 'address'];

/* ---------- small inline icons (no extra icon packages needed) ---------- */

const EyeIcon = () => (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
        strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <path d="M1.5 12S5.5 5 12 5s10.5 7 10.5 7-4 7-10.5 7S1.5 12 1.5 12z" />
        <circle cx="12" cy="12" r="3" />
    </svg>
);

const EyeOffIcon = () => (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
        strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
        <path d="M3 3l18 18" />
        <path d="M10.6 5.1A10.7 10.7 0 0 1 12 5c6.5 0 10.5 7 10.5 7a17.6 17.6 0 0 1-3.2 3.9" />
        <path d="M6.4 6.4A17.4 17.4 0 0 0 1.5 12S5.5 19 12 19a10.6 10.6 0 0 0 4.2-.9" />
        <path d="M9.9 9.9a3 3 0 0 0 4.2 4.2" />
    </svg>
);

/* The one illustration: a hotel key card, rated 4 out of 5 */
const KeyCard = () => {
    const star = '12,1.5 15.1,8.6 22.8,9.3 17,14.4 18.7,22 12,18 5.3,22 7,14.4 1.2,9.3 8.9,8.6';
    return (
        <svg className="auth-keycard" viewBox="0 0 320 200" aria-hidden="true">
            <rect x="4" y="4" width="312" height="192" rx="18" fill="#ffffff" />
            <rect x="4" y="34" width="312" height="38" fill="#0a4f5f" />
            {[0, 1, 2, 3, 4].map((i) => (
                <g key={i} transform={`translate(${28 + i * 38} 128) scale(1.25)`}>
                    <polygon points={star} fill={i < 4 ? '#12aed0' : '#cfe9f0'} />
                </g>
            ))}
            <text x="292" y="172" textAnchor="end" fontSize="15" fontWeight="700"
                fill="#0a4f5f" style={{ fontFamily: 'inherit' }}>
                HRS
            </text>
        </svg>
    );
};

const LogIn = () => {

    useEffect(() => {
        document.title = "HRS | Sign In";
    }, []);

    const [mode, setMode] = useState('signin');          // 'signin' | 'signup'
    const [user, setUser] = useState(EMPTY_USER);
    const [error, setError] = useState({ error: {}, isError: false });
    const [showPassword, setShowPassword] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    const handleForm = (e, fieldName) => {
        setUser({ ...user, [fieldName]: e.target.value });
    };

    // server-side validation message for one field (same shape as before)
    const fieldError = (name) => error?.error?.response?.data?.[name];
    const hasFieldErrors = FIELD_NAMES.some((n) => Boolean(fieldError(n)));

    const submitRegister = (event) => {
        event.preventDefault();
        setError({ error: {}, isError: false });
        setSubmitting(true);

        register(user)
            .then(() => {
                toast.success("Account created. You can sign in now.");
                setUser(EMPTY_USER);
                setMode('signin');
            })
            .catch((err) => {
                setError({ error: err, isError: true });
                console.error(err);
                toast.error("Couldn't create your account. Please check your details.");
            })
            .finally(() => setSubmitting(false));
    };

    const renderField = ({ id, name, label, type = 'text', autoComplete, placeholder }) => (
        <FormGroup className="auth-field" key={name}>
            <label className="auth-label" htmlFor={id}>{label}</label>
            <Input
                id={id}
                className="auth-input"
                type={type}
                name={name}
                value={user[name]}
                autoComplete={autoComplete}
                placeholder={placeholder}
                onChange={(e) => handleForm(e, name)}
                invalid={Boolean(fieldError(name))}
            />
            <FormFeedback>{fieldError(name)}</FormFeedback>
        </FormGroup>
    );

    return (
        <Base>
            <div className="auth-page">
                <div className="auth-shell">

                    {/* ---------------- Brand panel ---------------- */}
                    <aside className="auth-brand">
                        <p className="auth-brand-name">Hotel Rating System</p>
                        <h2 className="auth-brand-title">Honest ratings for every stay.</h2>
                        <p className="auth-brand-text">
                            Read what guests say about a hotel, then add your own review.
                        </p>
                        <KeyCard />
                    </aside>

                    {/* ---------------- Main side ---------------- */}
                    <main className="auth-main">

                        <div className="auth-switcher" role="group" aria-label="Choose sign in or create account">
                            <button type="button" aria-pressed={mode === 'signin'}
                                onClick={() => setMode('signin')}>
                                Sign in
                            </button>
                            <button type="button" aria-pressed={mode === 'signup'}
                                onClick={() => setMode('signup')}>
                                Create account
                            </button>
                        </div>

                        {/* ------------ Sign in (OAuth2) ------------ */}
                        {mode === 'signin' && (
                            <div className="auth-panel" key="signin">
                                <h1 className="auth-title">Welcome back</h1>
                                <p className="auth-lead">
                                    Sign in to rate hotels, read reviews and manage your profile.
                                </p>

                                <div className="auth-oauth">
                                    <LogInButton />
                                </div>

                                <p className="auth-note">
                                    You'll go to our secure sign-in page and return here afterwards.
                                </p>

                                <p className="auth-switch">
                                    New here?{' '}
                                    <button type="button" className="auth-link" onClick={() => setMode('signup')}>
                                        Create an account
                                    </button>
                                </p>
                            </div>
                        )}

                        {/* ------------ Create account ------------ */}
                        {mode === 'signup' && (
                            <div className="auth-panel" key="signup">
                                <h1 className="auth-title">Create your account</h1>
                                <p className="auth-lead">
                                    It takes a minute. You'll sign in right after.
                                </p>

                                {error.isError && !hasFieldErrors && (
                                    <div className="auth-alert" role="alert">
                                        We couldn't create your account. Check your details and try again.
                                    </div>
                                )}

                                <Form onSubmit={submitRegister} noValidate>
                                    {renderField({ id: 'reg-name', name: 'name', label: 'Full name', autoComplete: 'name' })}
                                    {renderField({ id: 'reg-email', name: 'email', label: 'Email', type: 'email', autoComplete: 'email' })}

                                    <FormGroup className="auth-field">
                                        <label className="auth-label" htmlFor="reg-password">Password</label>
                                        <div className="auth-pw">
                                            <Input
                                                id="reg-password"
                                                className="auth-input"
                                                type={showPassword ? 'text' : 'password'}
                                                name="password"
                                                value={user.password}
                                                autoComplete="new-password"
                                                onChange={(e) => handleForm(e, 'password')}
                                                invalid={Boolean(fieldError('password'))}
                                            />
                                            <button
                                                type="button"
                                                className="auth-pw-toggle"
                                                onClick={() => setShowPassword((s) => !s)}
                                                aria-label={showPassword ? 'Hide password' : 'Show password'}
                                            >
                                                {showPassword ? <EyeOffIcon /> : <EyeIcon />}
                                            </button>
                                            <FormFeedback>{fieldError('password')}</FormFeedback>
                                        </div>
                                    </FormGroup>

                                    <div className="auth-grid-2">
                                        {renderField({ id: 'reg-contact', name: 'contactNo', label: 'Mobile number', type: 'tel', autoComplete: 'tel' })}
                                        {renderField({ id: 'reg-address', name: 'address', label: 'Address', autoComplete: 'street-address' })}
                                    </div>

                                    <button type="submit" className="auth-btn" disabled={submitting}>
                                        {submitting ? 'Creating account...' : 'Create account'}
                                    </button>
                                </Form>

                                <p className="auth-switch">
                                    Already have an account?{' '}
                                    <button type="button" className="auth-link" onClick={() => setMode('signin')}>
                                        Sign in
                                    </button>
                                </p>
                            </div>
                        )}
                    </main>
                </div>
            </div>
        </Base>
    );
};

export default LogIn;
