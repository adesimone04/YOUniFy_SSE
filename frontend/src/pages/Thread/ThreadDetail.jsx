import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, Send, ArrowBigUp, ArrowBigDown } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

import AppNavbar from '../../components/layout/Navbar/AppNavbar';
import ThreadCard from '../../components/ui/ThreadCard/ThreadCard';
import CountUp from '../../components/ui/CountUp/CountUp';

const ThreadDetail = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [isMobile, setIsMobile] = useState(window.innerWidth < 1024);

    const thread = location.state?.thread;

    const [comments, setComments] = useState([
        { id: 1, author: "Marco Rossi", content: "Sono d'accordo con te, anche io ho avuto problemi simili.", timeAgo: "1h fa", avatar: "M", votes: 5, userVote: 0 },
        { id: 2, author: "Sara Lombardi", content: "Hai provato a controllare sul sito dell'ateneo?", timeAgo: "30m fa", avatar: "S", votes: 12, userVote: 0 },
        { id: 3, author: "Giovanni Bianchi", content: "Ottimo spunto, grazie!", timeAgo: "5m fa", avatar: "G", votes: 2, userVote: 0 },
        { id: 4, author: "Luca Verdi", content: "Seguo, interessa anche a me.", timeAgo: "2m fa", avatar: "L", votes: 1, userVote: 0 },
        { id: 5, author: "Elena Neri", content: "Attenzione che le scadenze sono cambiate quest'anno.", timeAgo: "1m fa", avatar: "E", votes: 8, userVote: 0 },
        { id: 6, author: "Francesco G.", content: "Qualcuno ha il link al bando aggiornato?", timeAgo: "30s fa", avatar: "F", votes: 0, userVote: 0 },
        { id: 7, author: "Anna B.", content: "Grazie mille per le info!", timeAgo: "10s fa", avatar: "A", votes: 1, userVote: 0 },
    ]);

    const [newComment, setNewComment] = useState("");
    const sortedComments = [...comments].sort((a, b) => b.votes - a.votes);

    if (!thread) {
        return (
            <div className="min-h-screen bg-transparent flex items-center justify-center text-white">
                <p>Discussione non trovata.</p>
                <button onClick={() => navigate('/thread')} className="ml-4 underline text-younify-blue">Torna ai thread</button>
            </div>
        );
    }

    const handleCommentUpvote = (id) => {
        setComments(prev => prev.map(c => {
            if (c.id !== id) return c;
            const newVote = c.userVote === 1 ? 0 : 1;
            const voteChange = newVote === 1 ? (c.userVote === -1 ? 2 : 1) : -1;
            return { ...c, userVote: newVote, votes: c.votes + voteChange };
        }));
    };

    const handleCommentDownvote = (id) => {
        setComments(prev => prev.map(c => {
            if (c.id !== id) return c;
            const newVote = c.userVote === -1 ? 0 : -1;
            const voteChange = newVote === -1 ? (c.userVote === 1 ? -2 : -1) : 1;
            return { ...c, userVote: newVote, votes: c.votes + voteChange };
        }));
    };

    const handleAddComment = (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;
        setComments([...comments, { id: Date.now(), author: "Tu", content: newComment, timeAgo: "Adesso", avatar: "T", votes: 0, userVote: 0 }]);
        setNewComment("");
    };

    return (
        <div className="fixed inset-0 w-full h-full font-sans text-slate-200 selection:bg-younify-blue/30 selection:text-white overflow-hidden flex bg-slate-900/60 backdrop-blur-md lg:bg-transparent lg:backdrop-blur-none">

            {/* 1. NAVBAR: Visibile SOLO su Desktop */}
            <div className="hidden lg:block">
                <AppNavbar />
            </div>

            {/* 2. CONTENITORE SCROLLABILE */}
            <div className="absolute inset-0 overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none'] z-0">

                <div className="max-w-3xl mx-auto w-full min-h-full flex flex-col pl-0 lg:pl-32 pr-0 lg:pr-8 pt-4 lg:pt-12">

                    {/* --- HEADER + THREAD --- */}
                    <div className="px-4 lg:px-0 mb-6">
                        {/* Header con tasto indietro */}
                        <motion.div
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                            transition={{ delay: 0.3, duration: 0.4 }}
                            className="flex items-center gap-4 mb-4"
                        >
                            <button onClick={() => navigate(-1)} className="p-3 bg-white/5 hover:bg-white/10 rounded-full text-white transition-colors border border-white/5 group">
                                <ArrowLeft size={24} className="group-hover:-translate-x-1 transition-transform"/>
                            </button>
                            <h1 className="text-2xl font-black text-white tracking-tight">Discussione</h1>
                        </motion.div>

                        {/* Thread Card UNIFICATA (Mobile & Desktop) */}
                        {/* Abbiamo rimosso trasformazioni e condizioni 'isMobile' complicate. */}
                        {/* Il layoutId fa tutto il lavoro di "magia" per tornare al posto giusto. */}
                        <motion.div
                            layoutId={`thread-card-${thread.id}`}
                            className="w-full mb-6"
                            transition={{ type: "spring", bounce: 0.2, duration: 0.6 }}
                        >
                            <ThreadCard thread={thread} showActions={false} />
                        </motion.div>
                    </div>

                    {/* --- LISTA COMMENTI --- */}
                    <div className="px-4 lg:px-0 pb-40 lg:pb-24">
                        <h3 className="text-lg font-bold text-white mb-4 ml-2 flex items-center gap-2">
                            Commenti <span className="text-slate-500 text-sm font-normal">({comments.length})</span>
                        </h3>

                        <div className="flex flex-col gap-4">
                            <AnimatePresence mode="popLayout">
                                {sortedComments.map((comment) => (
                                    <motion.div
                                        layout
                                        key={comment.id}
                                        initial={{ opacity: 0, y: 10 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        exit={{ opacity: 0, scale: 0.9 }}
                                        className="bg-slate-900/40 backdrop-blur-md border border-white/5 p-5 rounded-2xl flex gap-4 group hover:border-white/10 transition-colors"
                                    >
                                        <div className="w-10 h-10 rounded-full bg-slate-800 border border-white/10 flex items-center justify-center text-sm font-bold text-slate-400 shrink-0 shadow-inner">
                                            {comment.avatar}
                                        </div>
                                        <div className="flex-1 flex flex-col gap-2">
                                            <div className="flex justify-between items-baseline">
                                                <span className="font-bold text-white text-sm">{comment.author}</span>
                                                <span className="text-xs text-slate-500">{comment.timeAgo}</span>
                                            </div>
                                            <p className="text-slate-300 text-sm leading-relaxed mb-1">{comment.content}</p>

                                            <div className="flex items-center justify-end gap-3 mt-1">
                                                <div className="flex items-center gap-1 bg-black/20 px-2 py-1 rounded-lg border border-white/5">
                                                    <button onClick={() => handleCommentDownvote(comment.id)} className={`p-1 rounded hover:bg-white/10 transition-colors ${comment.userVote === -1 ? 'text-rose-500' : 'text-slate-500 hover:text-rose-400'}`}>
                                                        <ArrowBigDown size={18} fill={comment.userVote === -1 ? "currentColor" : "none"} />
                                                    </button>
                                                    <div className={`flex items-baseline gap-1 font-mono font-bold text-xs min-w-[30px] justify-center ${comment.userVote === 1 ? 'text-emerald-400' : comment.userVote === -1 ? 'text-rose-500' : 'text-slate-400'}`}>
                                                        <CountUp from={0} to={comment.votes} duration={0.5} />
                                                        <span className="text-[9px] font-sans">CFU</span>
                                                    </div>
                                                    <button onClick={() => handleCommentUpvote(comment.id)} className={`p-1 rounded hover:bg-white/10 transition-colors ${comment.userVote === 1 ? 'text-emerald-400' : 'text-slate-500 hover:text-emerald-400'}`}>
                                                        <ArrowBigUp size={18} fill={comment.userVote === 1 ? "currentColor" : "none"} />
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </motion.div>
                                ))}
                            </AnimatePresence>
                        </div>
                    </div>

                </div>
            </div>

            {/* --- BARRA INPUT FISSA TRASPARENTE --- */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.5 }}
                className="fixed bottom-0 left-0 right-0 z-30 pointer-events-none pb-4 pt-4 lg:pb-6 lg:pl-32 bg-transparent"
            >
                <div className="max-w-3xl mx-auto w-full px-4 lg:px-1 pointer-events-auto">
                    <form onSubmit={handleAddComment} className="relative flex items-center shadow-2xl shadow-black/50 rounded-full group">
                        <input
                            type="text"
                            maxLength={500}
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            placeholder="Scrivi un commento..."
                            className="w-full bg-slate-900/60 backdrop-blur-xl border border-white/10 text-white rounded-full py-4 pl-6 pr-14 focus:outline-none focus:border-younify-blue focus:ring-1 focus:ring-younify-blue transition-all placeholder:text-slate-500 shadow-inner"
                        />
                        <button type="submit" disabled={!newComment.trim()} className="absolute right-2 p-2.5 bg-younify-blue text-white rounded-full hover:bg-blue-600 disabled:opacity-50 disabled:bg-slate-800 transition-all active:scale-95">
                            <Send size={18} />
                        </button>
                    </form>
                </div>
            </motion.div>

        </div>
    );
};

export default ThreadDetail;