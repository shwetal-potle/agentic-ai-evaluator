import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { submitHackathonProject } from '../services/api';
import { UploadCloud, CheckCircle2 } from 'lucide-react';

const SubmissionUpload = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    teamName: '',
    projectTitle: '',
    useCaseDescription: '',
    architectureDetails: '',
    promptStrategy: ''
  });
  const [files, setFiles] = useState({
    pdfFile: null,
    zipFile: null
  });

  const handleFileChange = (e) => {
    setFiles({ ...files, [e.target.name]: e.target.files[0] });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const submissionForm = new FormData();
      submissionForm.append("submission", JSON.stringify(formData));
      if (files.pdfFile) submissionForm.append("pdfFile", files.pdfFile);
      if (files.zipFile) submissionForm.append("zipFile", files.zipFile);

      const response = await submitHackathonProject(submissionForm);
      navigate(`/evaluation/${response.id}`);
    } catch (error) {
      console.error("Upload failed", error);
      alert("Failed to submit project.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <header className="mb-10 text-center">
        <h1 className="text-4xl font-bold mb-4">Submit Project</h1>
        <p className="text-textMuted text-lg">
          Paste the details of the GenAI solution to be evaluated by the Multi-Agent system.
        </p>
      </header>

      <form onSubmit={handleSubmit} className="glass-card p-8 space-y-6">
        <div className="grid grid-cols-2 gap-6">
          <div className="space-y-2 text-left">
            <label className="text-sm font-medium text-textMuted">Team Name</label>
            <input
              required
              name="teamName"
              value={formData.teamName}
              onChange={handleChange}
              className="glass-input w-full"
              placeholder="e.g. Innovators"
            />
          </div>
          <div className="space-y-2 text-left">
            <label className="text-sm font-medium text-textMuted">Project Title</label>
            <input
              required
              name="projectTitle"
              value={formData.projectTitle}
              onChange={handleChange}
              className="glass-input w-full"
              placeholder="e.g. AI Support Agent"
            />
          </div>
        </div>

        <div className="space-y-2 text-left">
          <label className="text-sm font-medium text-textMuted">Use Case Description</label>
          <textarea
            required
            name="useCaseDescription"
            rows="4"
            value={formData.useCaseDescription}
            onChange={handleChange}
            className="glass-input w-full resize-none"
            placeholder="Describe the problem being solved..."
          />
        </div>

        <div className="space-y-2 text-left">
          <label className="text-sm font-medium text-textMuted">Architecture Details (Optional Code/Design)</label>
          <textarea
            name="architectureDetails"
            rows="5"
            value={formData.architectureDetails}
            onChange={handleChange}
            className="glass-input w-full font-mono text-sm resize-none"
            placeholder="Describe the architecture, stack, patterns, Database, API..."
          />
        </div>

        <div className="space-y-2 text-left">
          <label className="text-sm font-medium text-textMuted">Prompt Strategy & System Prompts</label>
          <textarea
            name="promptStrategy"
            rows="6"
            value={formData.promptStrategy}
            onChange={handleChange}
            className="glass-input w-full font-mono text-sm resize-none"
            placeholder="Include snippets of the exact prompts used, Guardrails, chains..."
          />
        </div>

        <div className="space-y-4 pt-4 border-t border-white/10">
          <h3 className="text-lg font-bold">Additional Resources (Optional)</h3>
          <div className="grid grid-cols-2 gap-6 p-4 bg-black/20 rounded-xl border border-white/5">
            <div className="space-y-2 text-left">
              <label className="text-sm font-medium text-textMuted flex items-center gap-2">
                <UploadCloud className="w-4 h-4 text-primary" />
                Architecture/Design PDF
              </label>
              <input
                type="file"
                name="pdfFile"
                accept=".pdf"
                onChange={handleFileChange}
                className="w-full text-sm text-textMuted file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary/20 file:text-primary hover:file:bg-primary/30"
              />
            </div>
            <div className="space-y-2 text-left">
              <label className="text-sm font-medium text-textMuted flex items-center gap-2">
                <UploadCloud className="w-4 h-4 text-accent" />
                Source Code ZIP
              </label>
              <input
                type="file"
                name="zipFile"
                accept=".zip"
                onChange={handleFileChange}
                className="w-full text-sm text-textMuted file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-accent/20 file:text-accent hover:file:bg-accent/30"
              />
            </div>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className={`w-full py-4 rounded-xl flex items-center justify-center gap-2 font-bold transition-all shadow-lg text-lg
            ${loading 
              ? 'bg-primary/50 cursor-not-allowed shadow-none' 
              : 'bg-primary hover:bg-primaryHover hover:-translate-y-1 shadow-primary/25'
            }`}
        >
          {loading ? (
            <div className="w-5 h-5 rounded-full border-2 border-white border-t-transparent animate-spin" />
          ) : (
            <UploadCloud className="w-5 h-5" />
          )}
          {loading ? 'Processing...' : 'Submit to AI Judges'}
        </button>
      </form>
    </div>
  );
};

export default SubmissionUpload;
